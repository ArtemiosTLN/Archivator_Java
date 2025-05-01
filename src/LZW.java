import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/***
 * ENG
 * LZW algorithm archiver
 * Made as a result of the computer science bachelor thesis titled "Development of the text archivers using linguistic features of the language"
 * Author: Artjom Šiškov
 * University: Tartu University
 * ---------------------------
 * Instructions:
 * 1. Build and run the LZW.java file with the desired Java IDE.
 * 2. Choose mode: 'e' for encoding and 'd' for decoding.
 * 3. For encoding:
 *      a. Navigate through the file system, starting from the "corpora" folder.
 *      b. Choose the file you want to encode. Files are marked in blue, folders marked in yellow.
 *      c. Choose the language the file is written in. est - Estonian, eng - English, rus - Russian and none - the language is not listed.
 *      d. After encoding the ".bin" file with the same name will be created and added to the "bin_files" folder in the project.
 *      Note: The system will tell you the data compression ratio given in % and the time spent in milliseconds.
 * 4. For decoding:
 *      a. Navigate through the file system, starting from the "bin_files" folder.
 *      b. Choose the file you want to decode. Files are marked in blue, folders marked in yellow.
 *      c. Wait until the file is decoded. All decoded files are added to the "decoded" folder.
 *      Note: The system will tell you the data decompression ratio given in % and the time spent in milliseconds.
 * EST
 * LZW algoritmi kasutatav arhiveerija.
 * Valminud bakalaureusetöö raames pealkirjaga "Tekstiarhiveerijate arendamine kasutades keelelisi iseärasusi"
 * Autor: Artjom Šiškov
 * Ülikool: Tartu Ülikool
 * ---------------------------
 * Juhised:
 * 1. Koosta ja käivita LZW.java fail soovitud Java IDE-s.
 * 2. Vali režiim: 'e' kodeerimiseks ja 'd' dekodeerimiseks.
 * 3. Kodeerimiseks:
 *      a. Liikuge failisüsteemis alustades kaustast "corpora".
 *      b. Valige fail kodeerimiseks. Failid on tähistatud sinise ja kaustad kollase värvidega.
 *      c. Valige keel: est - eesti, eng - inglise, rus - vene, none - keel ei ole loetletud.
 *      d. Pärast kodeerimist luuakse sama nimega ".bin" fail ja lisatakse see "bin_files" kausta projektis.
 *      Märkus: Süsteem kuvab andmete tihendamise suhte protsentides ja kulutatud aja millisekundites.
 * 4. Dekodeerimiseks:
 *      a. Liikuge failisüsteemis alustades kaustast "bin_files".
 *      b. Valige fail dekodeerimiseks. Failid on tähistatud sinise ja kaustad kollase värvidega.
 *      b. Oodake, kuni fail dekodeeritakse. Kõik dekodeeritud failid lisatakse kausta "decoded".
 *      Märkus: Süsteem kuvab andmete dekompressiooni suhte protsentides ja kulutatud aja millisekundites.
 * РУС
 * Архиватор, использующий алгоритм LZW
 * Разработан в рамках бакалаврской работы по теме "Разработка текстовых архиваторов с использованием лингвистических особенностей языка"
 * Автор: Артём Шишков
 * Университет: Тартуский университет
 * ---------------------------
 * Инструкции:
 * 1. Скомпилируйте и запустите файл LZW.java в выбранной среде разработки Java.
 * 2. Выберите режим: 'e' — для кодирования, 'd' — для декодирования.
 * 3. Для кодирования:
 *      a. Перемещайтесь по файловой системе, начиная с папки "corpora".
 *      b. Выберите файл, который хотите закодировать. Файлы отмечены синим, папки отмечены желтым.
 *      d. После кодирования будет создан файл с расширением ".bin" и тем же именем, который будет добавлен в папку "bin_files" проекта.
 *      Примечание: Система покажет коэффициент сжатия данных в процентах и затраченное время в миллисекундах.
 * 4. Для декодирования:
 *      а. Перемещайтесь по файловой системе, начиная с папки "bin_files".
 *      б. Выберите файл, который хотите декодировать. Файлы отмечены синим, папки отмечены желтым.
 *      в. Подождите, пока файл будет декодирован. Все декодированные файлы добавляются в папку "decoded".
 *      Примечание: Система покажет коэффициент восстановления данных в процентах и затраченное время в миллисекундах.
 */


public class LZW {
    public static LangDictionaryLZW LangDictionaryLZW;
    public static HashSet<Character> textSymbols;

    public static void main(String[] args) throws IOException {
        textSymbols = new HashSet<>();
        LangDictionaryLZW = new LangDictionaryLZW();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose mode (encode - e, decode - d):");
        boolean correct = false;
        String mode = scanner.nextLine();
        while (!correct) {
            correct = Objects.equals(mode, "e") || Objects.equals(mode, "d");
            if (!correct) {
                System.out.println("Please insert a correct order.");
                mode = scanner.nextLine();
            }
        }
        String lang = "";
        String text = "";
        long start;
        long finish;
        long startSize;
        long endSize;
        float rate;
        switch (mode) {
            case "e":
                File file = null;
                while (file == null) {
                    file = PrintFolderContent(new File("corpora"));
                }

                correct = false;
                while (!correct) {
                    System.out.println("Enter language (est, eng, rus, none):");
                    lang = scanner.nextLine();
                    if (Objects.equals(lang, "est") || Objects.equals(lang, "eng") || Objects.equals(lang, "rus") || Objects.equals(lang, "none")) {
                        text = ReadTextFile(file.getPath());
                        correct = true;
                    } else System.out.println("Try again");
                }

                System.out.println("Processing... Please wait.");
                start = System.currentTimeMillis();
                LangDictionaryLZW.setLang(lang);
                List<Short> code = EncodeText(text);
                String binPath = "bin_files/" + file.getName().substring(0, file.getName().length() - 3) + "bin";
                WriteBinFile(binPath, code, lang);
                finish = System.currentTimeMillis();
                System.out.println("Time spent: " + (finish - start) + " milliseconds.");
                startSize = Files.size(file.toPath());
                endSize = Files.size(Paths.get(binPath));
                System.out.println("File size before: " + startSize);
                System.out.println("After: " + endSize);
                rate = (float) endSize / (float) startSize * 100;
                System.out.println("Compression efficiency: " + rate + "%");
                break;

            case "d":
                File f = new File("bin_files");
                if (f.isDirectory()) {
                    if (f.listFiles() == null || Objects.requireNonNull(f.listFiles()).length == 0) {
                        System.out.println("No files to decode.");
                        return;
                    }
                }

                File binFile = null;
                while (binFile == null) {
                    binFile = PrintFolderContent(new File("bin_files"));
                }

                System.out.println("Processing... Please wait.");
                start = System.currentTimeMillis();
                String filePath = "decoded/" + binFile.getName().substring(0, binFile.getName().length() - 3) + "txt";
                WriteTextFile(filePath, DecodeText(ReadBinFile(binFile.getPath())));
                finish = System.currentTimeMillis();
                System.out.println("Time spent: " + (finish - start) + " milliseconds.");
                startSize = Files.size(Paths.get(filePath));
                endSize = Files.size(binFile.toPath());
                System.out.println("File size before: " + startSize);
                System.out.println("After: " + endSize);
                rate = (float) startSize / (float) endSize * 100;
                System.out.println("Decompression efficiency: " + rate + "%");
                break;
        }
    }

    public static File PrintFolderContent(File dir) throws IOException {
        Scanner sc = new Scanner(System.in);
        String yellow = "\u001B[33m";
        String blue = "\u001B[34m";
        String reset = "\u001B[0m";
        while (true) {
            boolean correct = false;
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files == null || files.length == 0) {
                    System.out.println("NB! Folder is empty!");
                    return null;
                }
                else {
                    int i = 0;
                    for (File file : files) {
                        i++;
                        if (file.isDirectory()) {
                            System.out.println(i + ". " + yellow + file.getName() + reset);
                        } else if (file.isFile()) {
                            System.out.println(i + ". " + blue + file.getName() + reset + " " + Files.size(file.toPath()) / 1000 + " Kilobytes");
                        } else System.out.println(i + ". " + file.getName());
                    }
                    while (!correct) {
                        try {
                            System.out.println("Type number: ");
                            int n = Integer.parseInt(0 + sc.nextLine());
                            if (n > files.length || n < 1) {
                                System.out.println("Invalid number, try again.");
                            } else {
                                correct = true;
                                dir = files[n - 1];
                            }
                        } catch (Exception e) {
                            System.out.println("Non number input detected. Please try again.");
                        }
                    }
                }
            } else if (dir.isFile()) {
                return dir;
            }
        }
    }

    public static List<Short> EncodeText(String text) {
        List<Short> result = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        String pair = "";
        int currentSymbol = addSymbolsToBuffer(buffer, text, 0);

        while (!buffer.isEmpty()) {
            String s = isStringHasAWord(buffer, LangDictionaryLZW.getFirstLetterList(buffer.charAt(0)));
            if (s == null) {
                if (!pair.isEmpty() && LangDictionaryLZW.isDictionaryNotFull()) {
                    LangDictionaryLZW.addWord(pair + buffer.charAt(0));
                }
                pair = buffer.substring(0, 1);
                buffer = new StringBuilder(buffer.substring(1));
            } else {
                buffer = new StringBuilder(buffer.substring(s.length()));
                if (!pair.isEmpty() && LangDictionaryLZW.isDictionaryNotFull()) {
                    LangDictionaryLZW.addWord(pair + s);
                }
                pair = s;
            }
            result.add(LangDictionaryLZW.getCode(pair));
            currentSymbol += addSymbolsToBuffer(buffer, text, currentSymbol);
        }
        return result;
    }

    public static int addSymbolsToBuffer(StringBuilder buffer, String text, int curSym) {
        int j;
        int limit = buffer.length();
        for (j = 0; j < LangDictionaryLZW.maximumLength - limit + 1 && curSym < text.length(); j++) {
            buffer.append(text.charAt(curSym));
            curSym++;
        }
        return j;
    }

    public static String isStringHasAWord(StringBuilder sb, TreeSet<String> words) {
        if (words == null || words.isEmpty()) {
            return null;
        }
        for (String s : words) {
            if (sb.toString().startsWith(s)) {
                return s;
            }
        }
        return null;
    }

    public static String DecodeText(List<Short> code) {
        StringBuilder result = new StringBuilder();
        StringBuilder buffer = new StringBuilder();
        for (Short b : code) {
            String s = LangDictionaryLZW.getWord(b);
            result.append(s);
            if (LangDictionaryLZW.isDictionaryNotFull()) {
                buffer.append(s);
                if (buffer.length() > 1) {
                    LangDictionaryLZW.addWord(buffer.toString());
                    buffer = new StringBuilder(s);
                }
            }
        }
        return result.toString();
    }

    public static String ReadTextFile(String filename) {
        StringBuilder result = new StringBuilder();
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8);
            int i;
            while ((i = reader.read()) != -1) {
                textSymbols.add((char) i);
                result.append((char) i);
            }
            reader.close();
            LangDictionaryLZW.addTextSymbols(textSymbols);
        } catch (IOException e) {
            throw new RuntimeException(e + "\nFile was not found in specified location.");
        }
        return result.toString();
    }

    public static void WriteTextFile(String filename, String text) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, StandardCharsets.UTF_8));
            writer.write(text);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e + "\nWriting failed.");
        }
    }

    public static void WriteBinFile(String filename, List<Short> codes, String lang) {
        try {
            FileOutputStream writer = new FileOutputStream(filename);
            DataOutputStream dos = new DataOutputStream(writer);
            dos.writeUTF(lang);
            dos.writeShort((short) textSymbols.size());
            for (Character textSymbol : textSymbols) {
                dos.writeChar(textSymbol);
            }
            for (Short code : codes) {
                dos.writeShort(code);
            }
            dos.close();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e + "\nWriting failed.");
        }
    }

    public static List<Short> ReadBinFile(String filename) {
        List<Short> result = new ArrayList<>();
        try {
            FileInputStream reader = new FileInputStream(filename);
            DataInputStream dis = new DataInputStream(reader);
            String lang = dis.readUTF();
            short i = dis.readShort();
            for (; i > 0; i--) {
                char c = dis.readChar();
                textSymbols.add(c);
            }
            while (dis.available() > 0) {
                result.add(dis.readShort());
            }
            dis.close();
            reader.close();
            LangDictionaryLZW.addTextSymbols(textSymbols);
            LangDictionaryLZW.setLang(lang);
        } catch (IOException e) {
            throw new RuntimeException(e + "\nFile was not found in specified location.");
        }
        return result;
    }
}
