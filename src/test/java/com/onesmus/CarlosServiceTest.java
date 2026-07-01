package com.onesmus;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CarlosServiceTest {

    private final CarlosService carlos = new CarlosService();

    @Test
    void hasApiKey_returnsFalseWithoutSpringContext() {
        assertThat(carlos.hasApiKey()).isFalse();
    }

    @Test
    void chat_helpReturnsHelpText() {
        String reply = carlos.chat("what can you do", new PacketStatistics(), List.of());
        assertThat(reply).contains("Carlos");
    }

    @Test
    void chat_statsWithNoPacketsReturnsNoPacketsMessage() {
        String reply = carlos.chat("give me a summary", new PacketStatistics(), List.of());
        assertThat(reply).contains("No packets captured yet");
    }

    @Test
    void chat_statusWithNoPacketsReturnsInactive() {
        String reply = carlos.chat("is capture running", new PacketStatistics(), List.of());
        assertThat(reply).contains("No packets captured");
    }

    @Test
    void chat_generalWithNoPacketsReturnsNoTraffic() {
        String reply = carlos.chat("analyze my network", new PacketStatistics(), List.of());
        assertThat(reply).contains("No traffic data yet");
    }
}
