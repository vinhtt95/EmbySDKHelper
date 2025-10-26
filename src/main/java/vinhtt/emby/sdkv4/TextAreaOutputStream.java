package vinhtt.emby.sdkv4;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Một OutputStream tùy chỉnh để chuyển hướng System.out đến một JavaFX TextArea.
 */
public class TextAreaOutputStream extends OutputStream {

    private TextArea textArea;

    public TextAreaOutputStream(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        // Chuyển hướng dữ liệu đến TextArea
        // Sử dụng Platform.runLater để đảm bảo việc cập nhật UI được thực hiện trên luồng JavaFX Application Thread
        Platform.runLater(() -> textArea.appendText(String.valueOf((char) b)));
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        String s = new String(b, off, len);
        Platform.runLater(() -> textArea.appendText(s));
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }
}