package io.gaia_app.runner.test_utils;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LastLogRecordHandler extends Handler {

    private LogRecord lastLog;

    @Override
    public void publish(LogRecord record) {
        this.lastLog = record;
    }

    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {}

    public LogRecord getLastLog() {
        return lastLog;
    }
}
