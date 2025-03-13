import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LZW {
    public static LangDictionaryLZW LangDictionaryLZW;
    public static HashSet<Character> textSymbols;

    public static void main(String[] args) {
        textSymbols = new HashSet<>();
        LangDictionaryLZW = new LangDictionaryLZW();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose mode (encode - e, decode - d):");
        String mode = scanner.nextLine();
        String filename;
        String lang;
        String text = "";
        switch (mode) {
            case "e":
                System.out.println("Enter file name (.txt):");
                filename = scanner.nextLine();
                System.out.println("Enter language (est, eng):");
                lang = scanner.nextLine();
                text = switch (lang) {
                    case "est" -> ReadTextFile("corpus_est/" + filename);
                    case "eng" -> ReadTextFile("corpus_eng/" + filename);
                    case "rus" -> ReadTextFile("corpus_rus/" + filename);
                    default -> text;
                };
                LangDictionaryLZW .setLang(lang);
                List<Short> code = EncodeText(text);
                WriteBinFile("bin_files/" + filename.substring(0, filename.length() - 3) + "bin", code, lang);
                break;
            case "d":
                System.out.println("Enter file name (.bin): ");
                filename = scanner.nextLine();
                WriteTextFile("decoded/" + filename.substring(0, filename.length() - 3) + "txt", DecodeText(ReadBinFile("bin_files/" + filename)));
                break;
        }
    }

    public static List<Short> EncodeText(String text) {
        List<Short> result = new ArrayList<>();
        ArrayDeque<Character> buffer = new ArrayDeque<>(LangDictionaryLZW.maximumLength + 1);
        String pair = "";
        int currentSymbol = addSymbolsToBuffer(buffer, text, 0);

        while (!buffer.isEmpty()) {
            String s = isStringHasAWord(buffer, LangDictionaryLZW.getFirstLetterList(buffer.peekFirst()));
            if (s == null) {
                if (!pair.isEmpty()) {
                    LangDictionaryLZW.addWord(pair + buffer.peekFirst());
                }
                pair = buffer.pop().toString();
            } else {
                removeSymbolsFromBuffer(buffer, s.length());
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

    public static void removeSymbolsFromBuffer(ArrayDeque<Character> buffer, int i) {
        for (int j = 0; j < i; j++) {
            buffer.pop();
        }
    }

    public static int addSymbolsToBuffer(ArrayDeque<Character> buffer, String text, int curSym) {
        int j;
        int limit = buffer.size();
        for (j = 0; j < LangDictionaryLZW.maximumLength - limit && curSym < text.length(); j++) {
            buffer.add(text.charAt(curSym));
            curSym++;
        }
        return j;
    }

    public static String isStringHasAWord(ArrayDeque<Character> symbols, TreeSet<String> words) {
        if (words == null || words.isEmpty()) {
            return null;
        }
        StringBuilder sym = new StringBuilder();
        for (char symbol : symbols) {
            sym.append(symbol);
        }
        for (String s : words) {
            if (sym.toString().startsWith(s)) {
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
            buffer.append(s);
            if (buffer.length() > s.length()) {
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

    public static void WriteBinFile(String filename, List<Short> codes, String lang) {
        try {
            FileOutputStream writer = new FileOutputStream(filename);
            DataOutputStream dos = new DataOutputStream(writer);
            dos.writeUTF(lang);
            for (Character textSymbol : textSymbols) {
                dos.writeChar(textSymbol);
            }
            dos.writeChar('½');
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
        boolean alphabetFormed = false;
        try {
            FileInputStream reader = new FileInputStream(filename);
            DataInputStream dis = new DataInputStream(reader);
            LangDictionaryLZW.setLang(dis.readUTF());
            while (dis.available() > 0) {
                if (!alphabetFormed) {
                    char c = dis.readChar();
                    if (c == '½') alphabetFormed = true;
                    else textSymbols.add(c);
                } else result.add(dis.readShort());
            }
            dis.close();
            reader.close();
            LangDictionaryLZW.addTextSymbols(textSymbols);
        } catch (IOException e) {
            throw new RuntimeException(e + "\nFile was not found in specified location.");
        }
        return result;
    }
}
