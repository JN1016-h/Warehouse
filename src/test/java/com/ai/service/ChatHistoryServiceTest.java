package com.ai.service;

import com.ai.dao.AiChatMessageDao;
import com.ai.dao.AiChatSessionDao;
import com.ai.entity.AiChatMessageEntity;
import com.ai.entity.AiChatSessionEntity;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ChatHistoryServiceTest {

    @Mock
    private AiChatSessionDao sessionDao;

    @Mock
    private AiChatMessageDao messageDao;

    @InjectMocks
    private ChatHistoryService chatHistoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getOrCreateSessionReturnsExisting() {
        AiChatSessionEntity existing = new AiChatSessionEntity();
        existing.setId(10L);
        existing.setUserId(1L);
        when(sessionDao.selectById(10L)).thenReturn(existing);

        AiChatSessionEntity result = chatHistoryService.getOrCreateSession(
                10L, 1L, "yonghu", "DETAILED", "库存分析");

        assertSame(existing, result);
        verify(sessionDao).updateById(existing);
        verify(sessionDao, never()).insert(any());
    }

    @Test
    public void getOrCreateSessionInsertsNew() {
        when(sessionDao.selectById(any())).thenReturn(null);
        doAnswer(inv -> {
            AiChatSessionEntity s = inv.getArgument(0);
            s.setId(99L);
            return 1;
        }).when(sessionDao).insert(any(AiChatSessionEntity.class));

        AiChatSessionEntity result = chatHistoryService.getOrCreateSession(
                null, 2L, "yonghu", null,
                "这是一个非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常长的问题标题");

        assertNotNull(result);
        assertEquals(99L, result.getId());
        assertEquals("SIMPLE", result.getStyle());
        assertTrue(result.getTitle().endsWith("..."));
        verify(sessionDao).insert(any(AiChatSessionEntity.class));
    }

    @Test
    public void getOrCreateSessionWrongUserCreatesNew() {
        AiChatSessionEntity existing = new AiChatSessionEntity();
        existing.setId(10L);
        existing.setUserId(99L);
        when(sessionDao.selectById(10L)).thenReturn(existing);

        chatHistoryService.getOrCreateSession(10L, 1L, "yonghu", "SIMPLE", "q");

        verify(sessionDao).insert(any(AiChatSessionEntity.class));
    }

    @Test
    public void saveMessage() {
        AiChatMessageEntity result = chatHistoryService.saveMessage(
                5L, "assistant", "回答", "{}", true, "INVENTORY");

        assertNotNull(result);
        assertEquals(5L, result.getSessionId());
        assertEquals(1, result.getDegraded().intValue());
        verify(messageDao).insert(any(AiChatMessageEntity.class));
    }

    @Test
    public void listSessionsNullUser() {
        assertTrue(chatHistoryService.listSessions(null).isEmpty());
        verify(sessionDao, never()).selectList(any());
    }

    @Test
    public void listSessionsReturnsList() {
        when(sessionDao.selectList(any(EntityWrapper.class)))
                .thenReturn(Arrays.asList(new AiChatSessionEntity()));

        List<AiChatSessionEntity> list = chatHistoryService.listSessions(1L);

        assertEquals(1, list.size());
    }

    @Test
    public void listMessagesUnauthorized() {
        when(sessionDao.selectById(1L)).thenReturn(null);

        assertTrue(chatHistoryService.listMessages(1L, 2L).isEmpty());
    }

    @Test
    public void listMessagesSuccess() {
        AiChatSessionEntity session = new AiChatSessionEntity();
        session.setUserId(2L);
        when(sessionDao.selectById(1L)).thenReturn(session);
        when(messageDao.selectList(any(EntityWrapper.class)))
                .thenReturn(Arrays.asList(new AiChatMessageEntity()));

        assertEquals(1, chatHistoryService.listMessages(1L, 2L).size());
    }

    @Test
    public void getMessageNotFound() {
        when(messageDao.selectById(1L)).thenReturn(null);
        assertNull(chatHistoryService.getMessage(1L, 2L));
    }

    @Test
    public void getMessageWrongUser() {
        AiChatMessageEntity msg = new AiChatMessageEntity();
        msg.setSessionId(5L);
        when(messageDao.selectById(1L)).thenReturn(msg);

        AiChatSessionEntity session = new AiChatSessionEntity();
        session.setUserId(99L);
        when(sessionDao.selectById(5L)).thenReturn(session);

        assertNull(chatHistoryService.getMessage(1L, 2L));
    }

    @Test
    public void getMessageSuccess() {
        AiChatMessageEntity msg = new AiChatMessageEntity();
        msg.setSessionId(5L);
        msg.setContent("ok");
        when(messageDao.selectById(1L)).thenReturn(msg);

        AiChatSessionEntity session = new AiChatSessionEntity();
        session.setUserId(2L);
        when(sessionDao.selectById(5L)).thenReturn(session);

        AiChatMessageEntity result = chatHistoryService.getMessage(1L, 2L);
        assertEquals("ok", result.getContent());
    }

    @Test
    public void getOrCreateSessionNullQuestionUsesDefaultTitle() {
        when(sessionDao.selectById(any())).thenReturn(null);
        ArgumentCaptor<AiChatSessionEntity> captor = ArgumentCaptor.forClass(AiChatSessionEntity.class);

        chatHistoryService.getOrCreateSession(null, 1L, "yonghu", "SIMPLE", null);

        verify(sessionDao).insert(captor.capture());
        assertEquals("新对话", captor.getValue().getTitle());
    }

    @Test
    public void getOrCreateSessionUpdatesStyleOnExisting() {
        AiChatSessionEntity existing = new AiChatSessionEntity();
        existing.setId(10L);
        existing.setUserId(1L);
        when(sessionDao.selectById(10L)).thenReturn(existing);

        chatHistoryService.getOrCreateSession(10L, 1L, "yonghu", "DETAILED", "q");

        assertEquals("DETAILED", existing.getStyle());
    }

    @Test
    public void getOrCreateSessionNullUserIdCreatesNew() {
        AiChatSessionEntity existing = new AiChatSessionEntity();
        existing.setId(10L);
        existing.setUserId(1L);
        when(sessionDao.selectById(10L)).thenReturn(existing);

        chatHistoryService.getOrCreateSession(10L, null, "yonghu", "SIMPLE", "q");

        verify(sessionDao).insert(any(AiChatSessionEntity.class));
    }

    @Test
    public void saveMessageNotDegraded() {
        AiChatMessageEntity result = chatHistoryService.saveMessage(
                5L, "user", "question", null, false, "GENERAL");
        assertEquals(0, result.getDegraded().intValue());
    }

    @Test
    public void listSessionsNullListFromDao() {
        when(sessionDao.selectList(any(EntityWrapper.class))).thenReturn(null);
        assertTrue(chatHistoryService.listSessions(1L).isEmpty());
    }

    @Test
    public void listMessagesNullUserId() {
        AiChatSessionEntity session = new AiChatSessionEntity();
        session.setUserId(2L);
        when(sessionDao.selectById(1L)).thenReturn(session);

        assertTrue(chatHistoryService.listMessages(1L, null).isEmpty());
    }

    @Test
    public void listMessagesNullListFromDao() {
        AiChatSessionEntity session = new AiChatSessionEntity();
        session.setUserId(2L);
        when(sessionDao.selectById(1L)).thenReturn(session);
        when(messageDao.selectList(any(EntityWrapper.class))).thenReturn(null);

        assertTrue(chatHistoryService.listMessages(1L, 2L).isEmpty());
    }

    @Test
    public void getMessageNullSession() {
        AiChatMessageEntity msg = new AiChatMessageEntity();
        msg.setSessionId(5L);
        when(messageDao.selectById(1L)).thenReturn(msg);
        when(sessionDao.selectById(5L)).thenReturn(null);

        assertNull(chatHistoryService.getMessage(1L, 2L));
    }

    @Test
    public void getMessageNullUserId() {
        AiChatMessageEntity msg = new AiChatMessageEntity();
        msg.setSessionId(5L);
        when(messageDao.selectById(1L)).thenReturn(msg);
        AiChatSessionEntity session = new AiChatSessionEntity();
        session.setUserId(2L);
        when(sessionDao.selectById(5L)).thenReturn(session);

        assertNull(chatHistoryService.getMessage(1L, null));
    }

    @Test
    public void truncateShortTitleNoEllipsis() {
        when(sessionDao.selectById(any())).thenReturn(null);
        ArgumentCaptor<AiChatSessionEntity> captor = ArgumentCaptor.forClass(AiChatSessionEntity.class);

        chatHistoryService.getOrCreateSession(null, 1L, "yonghu", "SIMPLE", "短标题");

        verify(sessionDao).insert(captor.capture());
        assertEquals("短标题", captor.getValue().getTitle());
    }
}
