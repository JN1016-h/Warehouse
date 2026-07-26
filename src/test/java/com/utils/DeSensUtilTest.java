package com.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DeSensUtil unit tests.
 */
public class DeSensUtilTest {

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDesensitizePageUtils() {
        SensitiveEntity entity = new SensitiveEntity();
        entity.setName("张三丰");
        entity.setPhone("13800138000");
        entity.setIdCard("110101199001011234");
        entity.setBankCard("6222021234567890123");
        entity.setEmail("zhangsan@example.com");

        PageUtils page = new PageUtils(Collections.singletonList(entity), 1, 10, 1);
        Map<String, String> rules = new HashMap<>();
        rules.put("name", "名");
        rules.put("phone", "手");
        rules.put("idCard", "身");
        rules.put("bankCard", "卡");
        rules.put("email", "邮");

        DeSensUtil.desensitize(page, rules);

        assertEquals("张**", entity.getName());
        assertEquals("138****8000", entity.getPhone());
        assertEquals("110101**********1234", entity.getIdCard());
        assertEquals("622202*********0123", entity.getBankCard());
        assertEquals("z*******@example.com", entity.getEmail());
    }

    @Test
    public void testDesensitizeListAndSingleObject() {
        SensitiveEntity e1 = new SensitiveEntity();
        e1.setName("李");
        SensitiveEntity e2 = new SensitiveEntity();
        e2.setName("王五");

        Map<String, String> rules = Collections.singletonMap("name", "名");
        DeSensUtil.desensitize(Arrays.asList(e1, null, e2), rules);
        assertEquals("李", e1.getName());
        assertEquals("王*", e2.getName());

        SensitiveEntity single = new SensitiveEntity();
        single.setPhone("123");
        DeSensUtil.desensitize(single, Collections.singletonMap("phone", "手"));
        assertEquals("123", single.getPhone());
    }

    @Test
    public void testDesensitizeNullPageAndEmptyRules() {
        DeSensUtil.desensitize((PageUtils) null, Collections.singletonMap("name", "名"));

        PageUtils emptyPage = new PageUtils(Collections.emptyList(), 0, 10, 1);
        DeSensUtil.desensitize(emptyPage, Collections.singletonMap("name", "名"));
    }

    @Test
    public void testUnknownRuleAndNonStringField() {
        SensitiveEntity entity = new SensitiveEntity();
        entity.setName("赵六");
        entity.setAge(30);

        Map<String, String> rules = new HashMap<>();
        rules.put("name", "未知规则");
        rules.put("age", "名");
        DeSensUtil.desensitize(entity, rules);

        assertEquals("赵六", entity.getName());
        assertEquals(30, entity.getAge());
    }

    @Test
    public void testDesensitizeEdgeCases() {
        SensitiveEntity entity = new SensitiveEntity();
        entity.setName(null);
        entity.setPhone("123");
        entity.setIdCard("123");
        entity.setBankCard("123456789");
        entity.setEmail("invalid");

        Map<String, String> rules = new HashMap<>();
        rules.put("name", "名");
        rules.put("phone", "手");
        rules.put("idCard", "身");
        rules.put("bankCard", "卡");
        rules.put("email", "邮");
        DeSensUtil.desensitize(entity, rules);

        assertNull(entity.getName());
        assertEquals("123", entity.getPhone());
        assertEquals("123", entity.getIdCard());
        assertEquals("123456789", entity.getBankCard());
        assertEquals("invalid", entity.getEmail());
    }

    public static class SensitiveEntity {
        private String name;
        private String phone;
        private String idCard;
        private String bankCard;
        private String email;
        private Integer age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }

        public String getBankCard() {
            return bankCard;
        }

        public void setBankCard(String bankCard) {
            this.bankCard = bankCard;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }
}
