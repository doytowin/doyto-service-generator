package win.doyto.service.generator.module.module;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import win.doyto.query.web.controller.AbstractRestController;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.annotation.Resource;

/**
 * 模块管理模块基本操作。
 *
 * @author Yuanzhen on 2016-07-22.
 */
@Slf4j
@RestController
@RequestMapping("/api/module")
public class ModuleController
        extends AbstractRestController<ModuleEntity, Integer, ModuleQuery, ModuleRequest, ModuleResponse>
        implements ModuleApi {

    @Getter
    @Resource
    private ModuleService service;

    @Override
    protected ModuleResponse buildResponse(ModuleEntity moduleEntity) {
        ModuleResponse moduleResponse = new ModuleResponse();
        BeanUtils.copyProperties(moduleEntity, moduleResponse);
        moduleResponse.setId(moduleEntity.getId());
        return moduleResponse;
    }

    @Override
    protected ModuleEntity buildEntity(ModuleRequest moduleRequest) {
        ModuleEntity moduleEntity = new ModuleEntity();
        BeanUtils.copyProperties(moduleRequest, moduleEntity);
        return moduleEntity;
    }

    @PostMapping("upload/{id}")
    public void upload(@PathVariable("id") String id, @RequestBody JSONObject json) {

        String rootPath = json.getString("root");
        JSONArray jsonArray = json.getJSONArray("data");

        if (StringUtils.isEmpty(rootPath)) {
            rootPath = "";
        }
        Runtime run = Runtime.getRuntime();
        for (int i = 0, len = jsonArray.size(); i < len; i++) {
            JSONObject data = jsonArray.getJSONObject(i);
            File file = new File(rootPath + File.separator + data.getString("path"));
            try {
                FileUtils.write(file, data.getString("text"), StandardCharsets.UTF_8);
                Process process = run.exec("git -c core.quotepath=false add -- " + file.getName(), null, file.getParentFile());
                log.info("git add result: {}", process.waitFor());
            } catch (IOException e) {
                log.error("文件写入失败", e);
                throw new RuntimeException("文件写入失败!", e);
            } catch (Exception e) {
                log.error("git添加失败", e);
            }
        }
    }
}