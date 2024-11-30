package com.util.codegenerate;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.util.codegenerate.fileManage.entity.TempFile;
import com.util.codegenerate.fileManage.mapper.TempFileMapper;
import freemarker.cache.ByteArrayTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@DS("w51sa35Mz9AW")
@Component
public class CustomFreeMarkerTemEngine extends AbstractTemplateEngine {
    @Autowired
    private TempFileMapper tempFileMapper;

    private Configuration configuration;

    public CustomFreeMarkerTemEngine() {
    }


    public @NotNull CustomFreeMarkerTemEngine init(@NotNull ConfigBuilder configBuilder) {
        this.configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        this.configuration.setDefaultEncoding(ConstVal.UTF8);
        return this;
    }

    public void writer(@NotNull Map<String, Object> objectMap, @NotNull String templatePath, @NotNull File outputFile) throws Exception {
        ByteArrayTemplateLoader byteArrayTemplateLoader = new ByteArrayTemplateLoader();
        TempFile tempFile = tempFileMapper.selectById(templatePath);
        if (tempFile == null) {
            throw new RuntimeException("模板文件不存在，请检查模板配置,模板id为:"+templatePath);
        }
        byteArrayTemplateLoader.putTemplate(tempFile.getFileName(), tempFile.getFile().getBytes(StandardCharsets.UTF_8));
        templatePath = tempFile.getFileName();
        this.configuration.setTemplateLoader(byteArrayTemplateLoader);
        Template template = this.configuration.getTemplate(templatePath);
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);

        try {
            template.process(objectMap, new OutputStreamWriter(fileOutputStream, ConstVal.UTF8));
        } catch (Throwable var9) {
            try {
                fileOutputStream.close();
            } catch (Throwable var8) {
                var9.addSuppressed(var8);
            }

            throw var9;
        }

        fileOutputStream.close();
        this.LOGGER.debug("模板:" + templatePath + ";  文件:" + outputFile);
    }

    public @NotNull String templateFilePath(@NotNull String filePath) {
        return filePath;
    }
}
