package ClinetGUI;

import javax.swing.*;
import java.util.concurrent.FutureTask;

public interface UpdateUINotifier {
    public void frameUpdate(FutureTask<Void> task);
}
