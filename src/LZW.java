import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
        String filename;
        String lang;
        String text = "";
        long start;
        long finish;
        long startSize;
        long endSize;
        float rate;
        switch (mode) {
            case "e":
                correct = false;
                System.out.println("Enter file name (.txt):");
                filename = scanner.nextLine();
                System.out.println("Enter language (est, eng, rus):");
                lang = scanner.nextLine();
                start = System.currentTimeMillis();
                while (!correct) {
                    try {
                        text = switch (lang) {
                            case "est" -> ReadTextFile("corpus_est/" + filename);
                            case "eng" -> ReadTextFile("corpus_eng/" + filename);
                            case "rus" -> ReadTextFile("corpus_rus/" + filename);
                            default -> text;
                        };
                        correct = true;
                    } catch (RuntimeException e) {
                        System.out.println("Please try again.");
                        System.out.println("Enter file name (.txt):");
                        filename = scanner.nextLine();
                        System.out.println("Enter language (est, eng, rus):");
                        lang = scanner.nextLine();
                    }
                }
                LangDictionaryLZW.setLang(lang);
                List<Integer> code = EncodeText(text);
                String binPath = "bin_files/" + filename.substring(0, filename.length() - 3) + "bin";
                WriteBinFile(binPath, code, lang);
                finish = System.currentTimeMillis();
                System.out.println("Time spent: " + (finish - start) + " milliseconds.");
                startSize = Files.size(Paths.get("corpus_" + lang + "/" + filename));
                endSize = Files.size(Paths.get(binPath));
                System.out.println("File size before: " + startSize);
                System.out.println("After: " + endSize);
                rate = (float) endSize / (float) startSize * 100;
                System.out.println("Compression efficiency: " + rate + "%");
                break;
            case "d":
                correct = false;
                System.out.println("Enter file name (.bin): ");
                filename = scanner.nextLine();
                start = System.currentTimeMillis();
                String filePath = "decoded/" + filename.substring(0, filename.length() - 3) + "txt";
                while (!correct) {
                    try {
                        WriteTextFile(filePath, DecodeText(ReadBinFile("bin_files/" + filename)));
                        correct = true;
                    } catch (RuntimeException e) {
                        System.out.println("Please try again.");
                        System.out.println("Enter file name (.bin): ");
                        filename = scanner.nextLine();
                    }
                }
                finish = System.currentTimeMillis();
                System.out.println("Time spent: " + (finish - start) + " milliseconds.");
                startSize = Files.size(Paths.get(filePath));
                endSize = Files.size(Paths.get("bin_files/" + filename));
                System.out.println("File size before: " + startSize);
                System.out.println("After: " + endSize);
                rate = (float) startSize / (float) endSize * 100;
                System.out.println("Decompression efficiency: " + rate + "%");
                break;
        }
    }

    public static List<Integer> EncodeText(String text) {
        List<Integer> result = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        String pair = "";
        int currentSymbol = addSymbolsToBuffer(buffer, text, 0);

        while (!buffer.isEmpty()) {
            String s = isStringHasAWord(buffer, LangDictionaryLZW.getFirstLetterList(buffer.charAt(0)));
            if (s == null) {
                if (!pair.isEmpty()) {
                    LangDictionaryLZW.addWord(pair + buffer.charAt(0));
                }
                pair = buffer.substring(0, 1);
                buffer = new StringBuilder(buffer.substring(1));
            } else {
                buffer = new StringBuilder(buffer.substring(s.length()));
                if (!pair.isEmpty()) {
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
        for (j = 0; j < LangDictionaryLZW.maximumLength - limit && curSym < text.length(); j++) {
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

    public static String DecodeText(List<Integer> code) {
        StringBuilder result = new StringBuilder();
        StringBuilder buffer = new StringBuilder();
        for (Integer b : code) {
            String s = LangDictionaryLZW.getWord(b);
            result.append(s);
            buffer.append(s);
            if (buffer.length() > 1) {
                LangDictionaryLZW.addWord(buffer.toString());
                buffer = new StringBuilder(s);
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

    public static void WriteBinFile(String filename, List<Integer> codes, String lang) {
        try {
            FileOutputStream writer = new FileOutputStream(filename);
            DataOutputStream dos = new DataOutputStream(writer);
            dos.writeUTF(lang);
            dos.writeInt(textSymbols.size());
            for (Character textSymbol : textSymbols) {
                dos.writeChar(textSymbol);
            }
            for (Integer code : codes) {
                dos.writeInt(code);
            }
            dos.close();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e + "\nWriting failed.");
        }
    }

    public static List<Integer> ReadBinFile(String filename) {
        List<Integer> result = new ArrayList<>();
        try {
            FileInputStream reader = new FileInputStream(filename);
            DataInputStream dis = new DataInputStream(reader);
            String lang = dis.readUTF();
            for (int i = dis.readInt(); i > 0; i--) {
                char c = dis.readChar();
                textSymbols.add(c);
            }
            while (dis.available() > 0) {
                result.add(dis.readInt());
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
