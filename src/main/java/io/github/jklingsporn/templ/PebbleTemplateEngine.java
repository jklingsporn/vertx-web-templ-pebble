package io.github.jklingsporn.templ;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Locale;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.Utils;
import io.vertx.ext.web.templ.TemplateEngine;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jklingsporn on 16.12.15.
 */
public class PebbleTemplateEngine implements TemplateEngine{

    private final PebbleEngine engine;

    public PebbleTemplateEngine() {
        this(new PebbleEngine.Builder().loader(new ClasspathLoader(Utils.getClassLoader())).build());
    }

    public PebbleTemplateEngine(PebbleEngine engine){
        this.engine = engine;
    }

    @Override
    public void render(RoutingContext context, String templateFileName, Handler<AsyncResult<Buffer>> handler) {
        try {
            StringWriter writer = new StringWriter();
            Map<String,Object> contextMap = new HashMap<>();
            contextMap.put("context", context);
            engine.getTemplate(templateFileName).evaluate(writer, contextMap, getLocale(context));
            Buffer buffer = Buffer.buffer(writer.toString());
            handler.handle(Future.succeededFuture(buffer));
        } catch (Throwable e) {
            handler.handle(Future.failedFuture(e));
        }
    }

    private java.util.Locale getLocale(RoutingContext context){
        final List<Locale> acceptableLocales = context.acceptableLocales();
        return acceptableLocales.size() == 0 ? toLocale(Locale.create()) : toLocale(acceptableLocales.get(0));
    }

    private java.util.Locale toLocale(Locale locale){
        return new java.util.Locale(locale.language(),locale.country(),locale.variant()==null?"":locale.variant());
    }


}
