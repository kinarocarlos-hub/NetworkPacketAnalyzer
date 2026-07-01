package com.onesmus;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PacketCaptureServiceTest {

    @Test
    void appProto_returnsExpectedProtocols() {
        PacketCaptureService svc = new PacketCaptureService(null);

        assertThat(svc.appProto(80)).isEqualTo("HTTP");
        assertThat(svc.appProto(443)).isEqualTo("HTTPS");
        assertThat(svc.appProto(53)).isEqualTo("DNS");
        assertThat(svc.appProto(67)).isEqualTo("DHCP");
        assertThat(svc.appProto(22)).isEqualTo("SSH");
        assertThat(svc.appProto(21)).isEqualTo("FTP");
        assertThat(svc.appProto(25)).isEqualTo("SMTP");
        assertThat(svc.appProto(110)).isEqualTo("POP3");
        assertThat(svc.appProto(143)).isEqualTo("IMAP");
        assertThat(svc.appProto(9999)).isEqualTo("UNKNOWN");
    }
}
