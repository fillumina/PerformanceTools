package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.ComposedName;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CvsFileExporterConsumer<A>
        implements PerformanceConsumer<A>, Serializable {
    private static final long serialVersionUID = 1L;

    private final PerformanceFormatter<A> delegate;
    private final File file;
    private FileWriter writer;

    public CvsFileExporterConsumer(PerformanceFormatter<A> delegate, File file) {
        this.delegate = delegate;
        this.file = file;
    }

    @Override
    public void consume(ComposedName message, A performances) {
        if (writer == null) {
            openFile();
        }

    }

    private void openFile() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
