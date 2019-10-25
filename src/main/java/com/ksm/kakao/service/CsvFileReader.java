package com.ksm.kakao.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
class CsvFileReader {
    private String filePath;
    private boolean containHeader;

    CsvFileReader(String filePath, boolean containHeader) {
        this.filePath = filePath;
        this.containHeader = containHeader;
    }

    void read(@NotNull Consumer<CSVRecord> lineListener) throws Exception {
        Objects.requireNonNull(lineListener, "line listener is null.");

        CSVParser parser = null;

        try {
            FileInputStream source = new FileInputStream(new File(filePath));
            parser = new CSVParser(
                    new InputStreamReader(source, StandardCharsets.UTF_8), containHeader ? CSVFormat.DEFAULT.withHeader() : CSVFormat.DEFAULT);

            for (CSVRecord record : parser) {
                lineListener.accept(record);
            }
        } finally {
            if (parser != null) {
                parser.close();
            }
        }
    }
}
