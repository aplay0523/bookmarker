package com.library.bookmarker.test;

import com.library.bookmarker.mapper.UserMapper;
import com.library.bookmarker.util.Sha256EncryptUtil;
import com.library.bookmarker.vo.UserVo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class userMapperTest {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private Sha256EncryptUtil sha256EncryptUtil;

    @Test
    void 유저_저장() {
        String providerId = sha256EncryptUtil.encryptSHA256("google-123");
        UserVo userVo = UserVo.builder()
                .id(1L)
                .age(10)
                .email("wjdtjr")
                .name("hong")
                .provider("google")
                .providerId(providerId)
                .build();
        int insertResult = userMapper.insertUser(userVo);
        System.out.print(providerId);

        // sha-256 암호화 검증
        assertThat(providerId).isEqualTo("285c3ad8116d0234decb0c47457dc6f29a1f24b615fd3cc128cb51975b7799b3");

        UserVo newUserVo = userMapper.selectUser("google",providerId);

        assertThat(insertResult).isEqualTo(1);
        assertThat(newUserVo).isNotNull();
        assertThat(newUserVo.getEmail()).isEqualTo("wjdtjr");
    }
}
