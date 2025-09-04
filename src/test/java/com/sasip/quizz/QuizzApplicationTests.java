package com.sasip.quizz;

import com.sasip.quizz.config.AppConfig; // ✅ Import your AppConfig
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import; // ✅ Import the @Import annotation

@SpringBootTest
@Import(AppConfig.class) // ✅ Add this line to include the RestTemplate bean
class QuizzApplicationTests {

    @Test
    void contextLoads() {
    }

}