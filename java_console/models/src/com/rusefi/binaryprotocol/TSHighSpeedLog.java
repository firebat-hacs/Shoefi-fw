package com.rusefi.binaryprotocol;

import com.rusefi.composite.CompositeEvent;
import com.rusefi.rusEFIVersion;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class TSHighSpeedLog extends StreamFile {
    private final String fileName;
    private int prevTime = 0;

    public TSHighSpeedLog(String fileName) {
        this.fileName = fileName;
    }

    private static void writeHeader(Writer writer) throws IOException {
        writer.write("#Firmware: console" + rusEFIVersion.CONSOLE_VERSION + " firmware " + rusEFIVersion.firmwareVersion.get() + "\n");
        writer.write("PriLevel,SecLevel,Trigger,Sync,Time,ToothTime\n" +
                "Flag,Flag,Flag,Flag,ms,ms\n");
    }

    @Override
    void append(List<CompositeEvent> events) {
        try {
            if (writer == null) {
                writer = new FileWriter(fileName);
                writeHeader(writer);
            }
            for (CompositeEvent event : events) {
                writer.write(event.isPrimaryTriggerAsInt() + "," + event.isSecondaryTriggerAsInt() + "," + event.isTrgAsInt() + "," + event.isSyncAsInt() + ",");
                int delta = event.getTimestamp() - prevTime;
                writer.write(event.getTimestamp() / 1000.0 + "," + delta / 1000.0 + "\n");
                prevTime = event.getTimestamp();
            }
            writer.flush();

        } catch (IOException e) {
            // ignoring IO exceptions
        }
    }

    @Override
    protected void writeFooter(FileWriter writer) throws IOException {
        writer.write("MARK 028\n");
    }
}
