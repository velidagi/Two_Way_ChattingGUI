import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class testing_server extends Thread {
    private JLabel label_s;
    private JLabel label_s2;
    private JPanel panel_s;
    private JLabel message_label;
    private JButton button1;
    private JTextField input_text_server;
    private JButton button_quit;
    private JTextArea textArea1;
    DataInputStream in;
    DataOutputStream out;
    static ArrayList<String> reading_array = new ArrayList<String>();
    ArrayList<String> sending_array = new ArrayList<String>();
    String ip;
    ArrayList<String> time_array = new ArrayList<String>();
    ArrayList<String> flag_array = new ArrayList<String>();
    public void log_writter_server(){
        try {
            FileWriter myWriter = new FileWriter("C:\\Users\\msi-nb\\IdeaProjects\\log_Gui\\src\\server_log.txt");
            int counter_send =0;
            int counter_read =0;

            for (int i = 0; i < flag_array.size(); i++) {
                String flag = flag_array.get(i);
                if (flag.equals("s")) {
                    myWriter.write(time_array.get(i)+"["+ip+"]"+"\tMe: "+sending_array.get(counter_send)+"\n");
                    counter_send++;
                }
                else if (flag.equals("r")) {
                    myWriter.write(time_array.get(i)+"["+ip+"]"+"\tClient: "+reading_array.get(counter_read)+"\n");
                    counter_read++;
                }
            }

            myWriter.close();
            System.out.println("Dosya başarıyla oluşturuldu ve yazıldı.");
        } catch (IOException z) {
            System.out.println("Bir hata oluştu.");
            z.printStackTrace();
        }
    }
    public void set_message_label(String message_label) {

        this.label_s.setText("Reading Message: "+message_label);
    }
    public void set_label_s (String label_s){
        this.message_label.setText("Sending Message: "+label_s);
    }
    public void chat_set_server(){
        textArea1.setText("");
        int counter_server =0;
        int counter_client =0;
        for (int j = 0; j < flag_array.size(); j++) {
            String flag = flag_array.get(j);
            if (flag.equals("s")) {
                textArea1.append(time_array.get(j)+"["+ip+"]"+"\tMe: "+sending_array.get(counter_server)+"\n");
                counter_server++;
            }
            else if (flag.equals("r")) {
                textArea1.append(time_array.get(j)+"["+ip+"]"+"\tClient: "+reading_array.get(counter_client)+"\n");
                counter_client++;
            }
        }
    }
    public void run() {
        String line = "";

        while (!line.equals("Over")) {
            try {
                line = in.readUTF();
                LocalDateTime date_time = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String time_now = date_time.format(formatter);
                time_array.add(time_now);
                set_message_label(line);
                flag_array.add("r");
                reading_array.add(line);
                chat_set_server();
            }

            catch (IOException i) {
                System.out.println(i + "\n\nServer closed...");
                    log_writter_server();
                    System.exit(0);
                    }
                }


    }
    public testing_server() {
        JFrame frame = new JFrame("Server GUI");
        frame.add(panel_s);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        //Pencere kapandı ve log kaydedilecek
        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.out.println("Closed");
                log_writter_server();
                e.getWindow().dispose();
            }
        });
        try {
            Socket socket;
            try (ServerSocket server = new ServerSocket(5000)) {
                System.out.println("Server started");
                System.out.println("Waiting for a client ...");
                socket = server.accept();
            }
            System.out.println("Client accepted");
            in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));

            out = new DataOutputStream(
                    socket.getOutputStream());
            InetAddress clientAddress = socket.getInetAddress();
            ip =clientAddress.getHostAddress();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DataOutputStream finalOut = out;
                String sending_message = input_text_server.getText();
                input_text_server.setText("");
                try {
                    finalOut.writeUTF(sending_message);
                    LocalDateTime date_time = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String time_now = date_time.format(formatter);
                    time_array.add(time_now);
                    set_label_s(sending_message);
                    flag_array.add("s");
                    sending_array.add(sending_message);
                    chat_set_server();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        button_quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Quit button");
                log_writter_server();
                System.exit(0);
            }
        });
    }
    public static void main(String[] args) {
        testing_server thread = new testing_server();
        thread.start();
    }
}
