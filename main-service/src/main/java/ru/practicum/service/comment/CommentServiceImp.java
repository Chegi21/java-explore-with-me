package ru.practicum.service.comment;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.enums.EventState;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.CommentEntity;
import ru.practicum.model.EventEntity;
import ru.practicum.model.UserEntity;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImp implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CommentShortDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        log.info("Запрос на создание комментария от пользователя с id = {} для события с id ={}", userId, eventId);

        EventEntity findEvent = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("Событие с id = {} не найдено", eventId);
            return new NotFoundException("Событие не найдено");
        });

        UserEntity findUser = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id = {} не найден", userId);
            return new NotFoundException("Пользователь не найден");
        });

        if (!findEvent.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Статус события должен быть PUBLISHED. Комментарий не может быть создан");
        }

        CommentEntity createComment = commentRepository.save(CommentMapper.toEntity(newCommentDto, findEvent, findUser));

        log.info("Комментарий  с id = {} успешно создан", createComment.getId());
        return CommentMapper.toShortDto(createComment);
    }

    @Transactional
    @Override
    public CommentShortDto updateComment(Long userId, Long commentId, NewCommentDto newCommentDto) {
        log.info("Запрос на обновление комментария с id = {} от пользователя с id = {}", commentId, userId);

        CommentEntity oldComment = commentRepository.findById(commentId).orElseThrow(() -> {
            log.warn("Комментарий с id = {} не найден", commentId);
            return new NotFoundException("Комментарий не найден");
        });

        UserEntity findUser = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id = {} не найден", userId);
            return new NotFoundException("Пользователь не найден");
        });

        if (!oldComment.getAuthor().equals(findUser)) {
            log.warn("Пользователь с id = {} не является автором комментария", userId);
            throw new BadRequestException("Пользователь не является автором комментария");
        }

        oldComment.setText(newCommentDto.getText());

        CommentEntity updateComment = commentRepository.save(oldComment);

        log.info("Комментарий  с id = {} успешно обновлен", updateComment.getId());
        return CommentMapper.toShortDto(updateComment);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentFullDto> getCommentListByUser(Long userId, PageRequest pageRequest) {
        log.info("Запрос от пользователя с id = {} на список своих комментариев", userId);

        UserEntity findUser = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id = {} не найден", userId);
            return new NotFoundException("Пользователь не найден");
        });

        List<CommentEntity> commentList = commentRepository.findByAuthor_Id(findUser.getId());

        log.info("Список комментариев в размере {} шт. от пользователя с id = {} успешно найден", commentList.size(), findUser.getId());
        return CommentMapper.toFullDtoList(commentList);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentFullDto> getCommentListByEvent(Long eventId, PageRequest pageRequest) {
        log.info("Запрос от администратора на список комментариев события с id = {} ", eventId);

        EventEntity findEvent = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("Событие с id = {} не найдено", eventId);
            return new NotFoundException("Событие не найдено");
        });

        List<CommentEntity> entityList = commentRepository.findAllByEvent_Id(findEvent.getId(), pageRequest);

        log.info("Список комментариев в размере {} шт. успешно найден", entityList.size());
        return CommentMapper.toFullDtoList(entityList);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentShortDto> search(String text, PageRequest pageRequest) {
        log.info("Запрос на список комментариев с текстом: {}", text);

        if (text == null || text.isBlank()) {
            List<CommentEntity> entityList = commentRepository.findAll(pageRequest).getContent();
            log.info("Найден список комментариев в размере {} шт.", entityList.size());
            return CommentMapper.toShortDtoList(entityList);
        }

        List<CommentEntity> entityList = commentRepository.search(text.toLowerCase(), pageRequest);

        log.info("Найден список комментариев в размере {} шт.", entityList.size());
        return CommentMapper.toShortDtoList(entityList);
    }

    @Transactional
    @Override
    public void deleteComment(Long userId, Long commentId) {
        log.info("Запрос от пользователя с id = {} на удаление своего комментария с id = {}", commentId, userId);

        CommentEntity findComment = commentRepository.findById(commentId).orElseThrow(() -> {
            log.warn("Комментарий с id = {} не найден", commentId);
            return new NotFoundException("Комментарий не найден");
        });

        UserEntity findUser = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id = {} не найден", userId);
            return new NotFoundException("Пользователь не найден");
        });

        if (!findComment.getAuthor().equals(findUser)) {
            log.warn("Пользователь с id = {} не является автором комментария", userId);
            throw new ValidationException("Пользователь не является автором комментария");
        }

        commentRepository.delete(findComment);

        log.info("Комментарий с id = {} успешно удален", commentId);
    }

    @Transactional
    @Override
    public void deleteCommentByAdmin(Long commentId) {
        log.info("Запрос от администратора на удаление комментария с id = {}", commentId);

        CommentEntity findComment = commentRepository.findById(commentId).orElseThrow(() -> {
            log.warn("Комментарий с id = {} не найден", commentId);
            return new NotFoundException("Комментарий не найден");
        });

        commentRepository.delete(findComment);

        log.info("Комментарий с id = {} успешно удален", commentId);
    }
}
