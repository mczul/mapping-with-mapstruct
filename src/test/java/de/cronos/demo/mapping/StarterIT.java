package de.cronos.demo.mapping;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("App Starter")
@DisplayNameGeneration(ReplaceUnderscores.class)
class StarterIT {

    @Test
    void should_provide_consistent_application_context() {
    }

}
