/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.github.paohaijiao.csv;

import com.github.paohaijiao.connector.JDataSetConnector;
import com.github.paohaijiao.dataset.JColumnMeta;
import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.dataset.JRow;
import com.github.paohaijiao.enums.JConnectorType;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * packageName com.github.paohaijiao.csv
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/26
 */
public class JCsvDataSetConnector implements JDataSetConnector {
    @Override
    public String getConnectorType() {
        return JConnectorType.csv.getCode();
    }

    @Override
    public JDataSet load(Object source) {
        if (source instanceof Path) {
            return convertFromPath((Path) source);
        } else if (source instanceof String) {
            return convertFromPath(Path.of((String) source));
        } else if (source instanceof Reader) {
            return convertFromReader((Reader) source);
        } else {
            throw new IllegalArgumentException("Unsupported CSV source type: " + source.getClass());
        }
    }
    private JDataSet convertFromPath(Path filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            return convertFromReader(reader);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSV file: " + filePath, e);
        }
    }

    private JDataSet convertFromReader(Reader reader) {
        try (BufferedReader br = new BufferedReader(reader)) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("CSV file is empty");
            }
            String[] headers = headerLine.split(",");
            List<JColumnMeta> columns = new ArrayList<>();
            for (String header : headers) {
                columns.add(new JColumnMeta(header.trim(), String.class, "csv"));
            }
            List<JRow> rows = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                JRow row = new JRow();
                for (int i = 0; i < headers.length; i++) {
                    String value = i < values.length ? values[i].trim() : "";
                    row.put(headers[i].trim(), value);
                }
                rows.add(row);
            }
            return new JDataSet(columns, rows);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV", e);
        }
    }
}
