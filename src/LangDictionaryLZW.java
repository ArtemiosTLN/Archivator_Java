import java.util.*;

public class LangDictionaryLZW {
    public LinkedHashMap<String, Integer> Dictionary;
    public HashMap<Character, TreeSet<String>> WordsByFirstLetter;
    public int maximumLength;
    private final String[] eng = {"ed ", "er ", " the ", " a ", " an ", " as ", "ing ", " with ", "th", " if ",
            " and ", " are ", "'re ", " am ", " by ", "he", "in", "er", "an", "re", "on", "at", "en", "nd", "ti",
            "es", "or", "te", "of", "ed", " is ", "it", "al", "ar", "st", "to", "nt", "oo", " what ", "n't ", "'s",
            "ion ", "ful ", "less ", " which ", " for ", " at ", " no ", " to ", " be", " not ", " from ", "ence ",
            "sh", "ch", "ly ", "ist ", "ll", "ous ", " re", " un", " de", " he ", " she ", " I ", " can ", " ha",
            " pre", " so "};
    private final String[] rus = {"нн", "енн", "ённ", "ян", "ый ", "ая ", "ое ", "ые ", "ий ", "ие ", "ость ", "ого ",
            "ой ", "ых ", "их ", "ые ", "ую ", "ому", "ым ", "им ", "ыми ", "ими ", "а ", "ов ", "ев ", "и ", "е ",
            "ам ", "у ", "ою ", "ами ", "ах ", "ом ", "ях ", "ями ", "ям ", "ей ", "ы ", " в", " во", " до", " за",
            " вы", " к", " меж", " на", " не", " ни", " о", " об", " от", " по", " под", " про", " с", " у", " без",
            " бес", " вос", " воз", " из", " ис", " раз", " рас", " пре", " при", "айш", "ее", "же", "ше", "ть", "аю ",
            "ет ", "ем ", "ют ", "л ", "ли ", "ла ", "ло ", "ся ", "сь ", "чи", "ши", " бы ", " я ", " ты ", " мы ",
            " вы ", " он"};
    private final String[] est = {"sse", "st", "le", "lt", "ks", "ni ", "na ", "ta", "ga ", "te", "de", "id ",
            "im ", "em ", "ma", "da", "ev ", "nud ", "tud ", "dud ", "takse ", "dakse ", "akse ", "me ", "äe",
            "ti", "di", "si", " ol", "ei", " ja ", "vat ", "n ", "b ", "d ", "vad ", " är", "nd", "hk", "tu",
            "ku ", "ne", "se", "oi", "ke ", "kku ", "ku ", "õi", "ai", "ld", "va", "mb", "br", "pr", "hv",
            "aa", "ee", "uu", "üü", "ää", "öö", "ii", "oo", "nn", "mm", "ll", " see ", "kk", ", et ", "it ",
            "pp", "sid ", "seid ", "ks", "ss", "ea", "au", "ki ", "gi ", " kel", " mil", " on ", " sell"};

    public LangDictionaryLZW() {
        Dictionary = new LinkedHashMap<>();
        WordsByFirstLetter = new HashMap<>();
        maximumLength = 0;
    }

    public void addTextSymbols(HashSet<Character> symbols) {
        for (Character textSymbol : symbols) {
            this.addWord(String.valueOf(textSymbol));
        }
    }
    public void setLang(String lang) {
        switch (lang) {
            case "est":
                for (String s : est) {
                    this.addWord(s);
                }
                break;
            case "eng":
                for (String s : eng) {
                    this.addWord(s);
                }
                break;
            case "rus":
                for (String s : rus) {
                    this.addWord(s);
                }
                break;
            case "none":
                break;
        }
    }
    public void addWord(String word) {
        if (word.length() > 1) {
            Dictionary.put(word, 0);
            if (WordsByFirstLetter.containsKey(word.charAt(0))) {
                WordsByFirstLetter.get(word.charAt(0)).add(word);
            } else WordsByFirstLetter.put(word.charAt(0), new TreeSet<>(Comparator.reverseOrder()){{add(word);}});
            if (maximumLength < word.length()) maximumLength = word.length();
        } else Dictionary.put(word, -1);
    }
    public Short getCode(String word) {
        short i = Short.MIN_VALUE;
        for (String s : Dictionary.keySet()) {
            if (word.equals(s)) {
                incrementCounter(word);
                return i;
            } else i++;
        }
        return -1;
    }
    public void incrementCounter(String s) {
        if (s.length() > 1) Dictionary.put(s, Dictionary.get(s) + 1);
    }
    public String getWord(Short b) {
        short i = Short.MIN_VALUE;
        for (String s : Dictionary.keySet()) {
            if (i == b) {
                incrementCounter(s);
                return s;
            }
            else i++;
        }
        return null;
    }
    public TreeSet<String> getFirstLetterList(Character firstLetter) {
        return WordsByFirstLetter.get(firstLetter);
    }
    public boolean isDictionaryNotFull() {
        return Dictionary.size() < Short.MAX_VALUE * 2;
    }
    public void removeRedundantWords() {
        ArrayList<String> toRemove = new ArrayList<>();
        int i = Dictionary.size() - 100;
        for (String s : Dictionary.keySet()) {
            if (Dictionary.get(s) == 0) toRemove.add(s);
            i--;
            if (i >= 0) break;
        }
        if (!toRemove.isEmpty()) {
            for (String s : toRemove) {
                Dictionary.remove(s);
            }
        }
    }
}
