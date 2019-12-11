package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.JsonNodeValueResolver;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.cache.GuavaTemplateCache;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * This class loads the handlebar templates
 */
public final class HandlebarsTemplateLoader {
    private Handlebars handlebars;

    public HandlebarsTemplateLoader() {
        loadHandlebarTemplates();
    }

    /**
     * Get the compiled template
     *
     * @param templateName - name of the template to compile
     * @return the compiled template
     * @throws IOException
     */
    public Template getTemplate(String templateName) throws IOException {
        Template template = this.getHandlebars().compile(templateName);
        return template;
    }

    /**
     * The Context for the template
     */
    public Context getContext(JsonNode model) {
        Context context = Context
                .newBuilder(model)
                .resolver(JsonNodeValueResolver.INSTANCE, JavaBeanValueResolver.INSTANCE, FieldValueResolver.INSTANCE,
                        MapValueResolver.INSTANCE,
                        MethodValueResolver.INSTANCE)
                .build();
        return context;
    }

    private void loadHandlebarTemplates() {
        TemplateLoader loader = new ClassPathTemplateLoader("/templates", ".hbs");
        final Cache<TemplateSource, Template> templateCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();
        setHandlebars(new Handlebars(loader).with((new GuavaTemplateCache(templateCache))));
    }

    public Handlebars getHandlebars() {
        return handlebars;
    }

    private void setHandlebars(Handlebars handlebars) {
        this.handlebars = handlebars;
    }
}