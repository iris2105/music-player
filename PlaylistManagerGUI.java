import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

class Song {
    String title;
    String artist;
    int duration;

    public Song(String title, String artist, int duration) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return title + " by " + artist + " [" + duration + " sec]";
    }
}

class Playlist {
    private LinkedList<Song> songs;

    public Playlist() {
        songs = new LinkedList<>();
    }

    public void addSong(String title, String artist, int duration) {
        songs.add(new Song(title, artist, duration));
    }

    public void removeSong(String title) {
        songs.removeIf(song -> song.title.equalsIgnoreCase(title));
    }

    public String displayPlaylist() {
        if (songs.isEmpty()) {
            return "Playlist is empty.";
        }
        StringBuilder display = new StringBuilder();
        for (int i = 0; i < songs.size(); i++) {
            display.append((i + 1) + ". " + songs.get(i) + "\n");
        }
        return display.toString();
    }

    public Song searchSong(String title) {
        for (Song song : songs) {
            if (song.title.equalsIgnoreCase(title)) {
                return song;
            }
        }
        return null;
    }

    public void reorderSongs(int fromPos, int toPos) {
        if (fromPos > 0 && fromPos <= songs.size() && toPos > 0 && toPos <= songs.size()) {
            Song song = songs.remove(fromPos - 1);
            songs.add(toPos - 1, song);
        }
    }

    public void shufflePlaylist() {
        Collections.shuffle(songs, new Random());
    }

    public void sortPlaylistByTitle() {
        songs.sort((s1, s2) -> s1.title.compareToIgnoreCase(s2.title));
    }

    public void sortPlaylistByArtist() {
        songs.sort((s1, s2) -> s1.artist.compareToIgnoreCase(s2.artist));
    }

    public void saveToFile(String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (Song song : songs) {
            writer.write(song.title + "," + song.artist + "," + song.duration + "\n");
        }
        writer.close();
    }

    public void loadFromFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        songs.clear();
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 3) {
                String title = parts[0];
                String artist = parts[1];
                int duration = Integer.parseInt(parts[2]);
                addSong(title, artist, duration);
            }
        }
        reader.close();
    }
}

public class PlaylistManagerGUI extends JFrame implements ActionListener {
    private Playlist playlist = new Playlist();
    private JTextArea displayArea;
    private JTextField titleField, artistField, durationField, searchField, fromPosField, toPosField;
    private JFileChooser fileChooser = new JFileChooser();

    public PlaylistManagerGUI() {
        setTitle("Music Playlist Manager");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Playlist Display Area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.CENTER);

        // Input Panel for adding and removing songs
        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        titleField = new JTextField();
        artistField = new JTextField();
        durationField = new JTextField();
        searchField = new JTextField();
        fromPosField = new JTextField();
        toPosField = new JTextField();

        inputPanel.add(new JLabel("Song Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Artist:"));
        inputPanel.add(artistField);
        inputPanel.add(new JLabel("Duration (seconds):"));
        inputPanel.add(durationField);
        inputPanel.add(new JLabel("Search Title:"));
        inputPanel.add(searchField);
        inputPanel.add(new JLabel("Move from Pos:"));
        inputPanel.add(fromPosField);
        inputPanel.add(new JLabel("Move to Pos:"));
        inputPanel.add(toPosField);

        add(inputPanel, BorderLayout.NORTH);

        // Button Panel for operations
        JPanel buttonPanel = new JPanel(new GridLayout(3, 4));
        addButton(buttonPanel, "Add Song", "ADD");
        addButton(buttonPanel, "Remove Song", "REMOVE");
        addButton(buttonPanel, "Search Song", "SEARCH");
        addButton(buttonPanel, "Reorder Songs", "REORDER");
        addButton(buttonPanel, "Shuffle Playlist", "SHUFFLE");
        addButton(buttonPanel, "Sort by Title", "SORT_TITLE");
        addButton(buttonPanel, "Sort by Artist", "SORT_ARTIST");
        addButton(buttonPanel, "Save Playlist", "SAVE");
        addButton(buttonPanel, "Load Playlist", "LOAD");
        addButton(buttonPanel, "Display Playlist", "DISPLAY");
        addButton(buttonPanel, "Exit", "EXIT");

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addButton(JPanel panel, String label, String command) {
        JButton button = new JButton(label);
        button.setActionCommand(command);
        button.addActionListener(this);
        panel.add(button);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "ADD":
                String title = titleField.getText();
                String artist = artistField.getText();
                int duration = Integer.parseInt(durationField.getText());
                playlist.addSong(title, artist, duration);
                displayArea.setText("Song added: " + title);
                break;
            case "REMOVE":
                title = titleField.getText();
                playlist.removeSong(title);
                displayArea.setText("Song removed: " + title);
                break;
            case "SEARCH":
                title = searchField.getText();
                Song song = playlist.searchSong(title);
                if (song != null) {
                    displayArea.setText("Song found: " + song);
                } else {
                    displayArea.setText("Song not found.");
                }
                break;
            case "REORDER":
                int fromPos = Integer.parseInt(fromPosField.getText());
                int toPos = Integer.parseInt(toPosField.getText());
                playlist.reorderSongs(fromPos, toPos);
                displayArea.setText("Songs reordered.");
                break;
            case "SHUFFLE":
                playlist.shufflePlaylist();
                displayArea.setText("Playlist shuffled.");
                break;
            case "SORT_TITLE":
                playlist.sortPlaylistByTitle();
                displayArea.setText("Playlist sorted by title.");
                break;
            case "SORT_ARTIST":
                playlist.sortPlaylistByArtist();
                displayArea.setText("Playlist sorted by artist.");
                break;
            case "SAVE":
                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        playlist.saveToFile(fileChooser.getSelectedFile().getPath());
                        displayArea.setText("Playlist saved.");
                    } catch (IOException ex) {
                        displayArea.setText("Error saving playlist.");
                    }
                }
                break;
            case "LOAD":
                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        playlist.loadFromFile(fileChooser.getSelectedFile().getPath());
                        displayArea.setText("Playlist loaded.");
                    } catch (IOException ex) {
                        displayArea.setText("Error loading playlist.");
                    }
                }
                break;
            case "DISPLAY":
                displayArea.setText(playlist.displayPlaylist());
                break;
            case "EXIT":
                System.exit(0);
                break;
        }
    }

    public static void main(String[] args) {
        new PlaylistManagerGUI();
    }
}
