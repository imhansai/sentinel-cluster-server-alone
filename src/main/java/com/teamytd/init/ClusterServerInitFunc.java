package com.teamytd.init;

import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterFlowRuleManager;
import com.alibaba.csp.sentinel.datasource.FileRefreshableDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class ClusterServerInitFunc implements InitFunc {

    @Override
    public void init() throws Exception {
        File file;

        URL url = this.getClass().getResource("/rules/FlowRule.json");
        file = Paths.get(Objects.requireNonNull(url, "流控规则文件为空").toURI()).toFile();

        // try (InputStream inputStream = this.getClass().getResourceAsStream("/rules/FlowRule.json")) {
        //     file = File.createTempFile("FlowRule-", ".json");
        //     Files.copy(Objects.requireNonNull(inputStream), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        // }

        // Data source for FlowRule
        FileRefreshableDataSource<List<FlowRule>> flowRuleDataSource = new FileRefreshableDataSource<>(file, source -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(source, new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        ClusterFlowRuleManager.setPropertySupplier(namespace -> flowRuleDataSource.getProperty());
    }

}