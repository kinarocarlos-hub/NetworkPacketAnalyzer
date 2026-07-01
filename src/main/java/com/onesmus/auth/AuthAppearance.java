package com.onesmus.auth;

import org.springframework.stereotype.Component;

@Component
public class AuthAppearance {

    public record Options(
            boolean animations,
            String logoUrl,
            String logoLink,
            String helpPageUrl,
            String termsPageUrl,
            String privacyPageUrl) {

        public static Options defaults() {
            return new Options(true, null, null, null, null, null);
        }
    }

    public record Variables(
            String primaryColor,
            String background,
            String foreground,
            String fontFamily,
            String borderRadius) {

        public static Variables defaults() {
            return new Variables("#6366f1", "#0a0f1e", "#ffffff", null, "0.5rem");
        }
    }

    public record Elements(
            String card,
            String input,
            String buttonPrimary) {

        public static Elements defaults() {
            return new Elements(null, null, null);
        }
    }

    private Options options = Options.defaults();
    private Variables variables = Variables.defaults();
    private Elements elements = Elements.defaults();

    public void configure(Options opts, Variables vars, Elements els) {
        this.options = opts != null ? opts : Options.defaults();
        this.variables = vars != null ? vars : Variables.defaults();
        this.elements = els != null ? els : Elements.defaults();
    }

    public Options options() { return options; }
    public Variables variables() { return variables; }
    public Elements elements() { return elements; }
}