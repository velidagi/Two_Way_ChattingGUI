import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class testing_client extends Thread {
    private JButton button1;
    private JTextField textField1;
    private JLabel l;
    JPanel panel1;
    private JLabel reading_message_client;
    private JButton button_quit;
    private JTextArea textArea1;
    private final Socket socket;
    static ArrayList<String> reading_array = new ArrayList<String>();
    static ArrayList<String> sending_array = new ArrayList<String>();
    static String ip;
    static ArrayList<String> time_array = new ArrayList<String>();
    static ArrayList<String> flag_array = new ArrayList<String>();
    public void log_writer(){
        try {
            FileWriter my_writer = new FileWriter("client_log.txt");
            int counter_server =0;
            int counter_client =0;
            for (int j = 0; j < flag_array.size(); j++) {
                String flag = flag_array.get(j);
                if (flag.equals("s")) {
                    my_writer.write(time_array.get(j)+"["+ip+"]"+"\tMe: "+sending_array.get(counter_server)+"\n");
                    counter_server++;
                }
                else if (flag.equals("r")) {
                    my_writer.write(time_array.get(j)+"["+ip+"]"+"\tServer: "+reading_array.get(counter_client)+"\n");
                    counter_client++;
                }
            }
            my_writer.close();
            System.out.println("Dosya başarıyla oluşturuldu ve yazıldı.");
        } catch (IOException z) {
            System.out.println("Bir hata oluştu.");
            z.printStackTrace();
        }
        System.out.println("\n\nClient closed...");
        System.exit(0);
    }
    public void chat_setting (){
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
                textArea1.append(time_array.get(j)+"["+ip+"]"+"\tServer: "+reading_array.get(counter_client)+"\n");
                counter_client++;
            }
        }
    }

    public void run(){
        System.out.println("Thread is running...");
        try {
            DataInputStream in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));
            String line = "";
            while (!line.equals("Over"))
            {
                line = in.readUTF();
                LocalDateTime date_time = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String time_now = date_time.format(formatter);
                time_array.add(time_now);
                flag_array.add("r");
                reading_array.add(line);
                reading_message_client.setText("Reading Message: "+line);
                chat_setting();

            }
        }
        catch(IOException i)
        {
            log_writer();
        }
    }
    public testing_client() {

        DataOutputStream out;
        try {
            socket = new Socket("127.0.0.1", 5000);
            System.out.println("Connected");
            l.setText("Connection Successful! Please enter the message:");
            DataInputStream input = new DataInputStream(System.in);
            out = new DataOutputStream(
                    socket.getOutputStream());
            InetAddress clientAddress = socket.getLocalAddress();
            ip = clientAddress.getHostAddress();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DataOutputStream finalOut = out;
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = textField1.getText();
                try {
                    finalOut.writeUTF(message);
                    LocalDateTime date_time = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String time_now = date_time.format(formatter);
                    time_array.add(time_now);
                    flag_array.add("s");
                    sending_array.add(message);
                    l.setText("Sending:\t'"+textField1.getText()+"'");
                    textField1.setText("");
                    chat_setting();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        button_quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Quit button");
                log_writer();
                System.exit(0);
            }
        });
    }
    public static void main(String[] args) {
        // establish a connection
        JFrame frame = new JFrame("Client GUI");
        testing_client t = new testing_client();
        t.start();
        frame.setContentPane(t.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.out.println("Closed");
                try {
                    FileWriter my_writter = new FileWriter("C:\\Users\\msi-nb\\IdeaProjects\\log_Gui\\src\\client_log.txt");
                    int counter_server =0;
                    int counter_client =0;
                    for (int j = 0; j < flag_array.size(); j++) {
                        String flag = flag_array.get(j);
                        if (flag.equals("s")) {
                            my_writter.write(time_array.get(j)+"["+ip+"]"+"\tMe: "+sending_array.get(counter_server)+"\n");
                            counter_server++;
                        }
                        else if (flag.equals("r")) {
                            my_writter.write(time_array.get(j)+"["+ip+"]"+"\tServer: "+reading_array.get(counter_client)+"\n");
                            counter_client++;
                        }
                    }
                    my_writter.close();
                    System.out.println("Dosya başarıyla oluşturuldu ve yazıldı.");
                } catch (IOException z) {
                    System.out.println("Bir hata oluştu.");
                    z.printStackTrace();
                }
                e.getWindow().dispose();
            }
        });
    }
}
