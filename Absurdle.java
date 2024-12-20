/*
Angel Arellano
Absurdle
The purpose of this class is to make a game similar to the New York Times game, wordle.
The game prompts the user to enter a dictionary file and length of word they wish to play with.
The user then enters a word to try and guess the word that the game has chosen. If the player
enters a word and a letter is in the right spot when compared correct word, the word they typed in
will have the character that was placed in the exact location be replaced with a green square. If
the user enters a character thats word contains but is not in the right spot, that character will
be replaced by a yellow square. But the twist is that the game changes the word everytime the
player makes a guess in order to keep the game going for as long as possible.
*/
import java.util.*;
import java.io.*;

public class Absurdle  {
    public static final String GREEN = "🟩";
    public static final String YELLOW = "🟨";
    public static final String GRAY = "⬜";

    public static void main(String[] args) throws FileNotFoundException {
        Scanner console = new Scanner(System.in);
        System.out.println("Welcome to the game of Absurdle.");

        System.out.print("What dictionary would you like to use? ");
        String dictName = console.next();

        System.out.print("What length word would you like to guess? ");
        int wordLength = console.nextInt();

        List<String> contents = loadFile(new Scanner(new File(dictName)));
        Set<String> words = pruneDictionary(contents, wordLength);

        List<String> guessedPatterns = new ArrayList<>();
        while (!isFinished(guessedPatterns)) {
            System.out.print("> ");
            String guess = console.next();
            String pattern = record(guess, words, wordLength);
            guessedPatterns.add(pattern);
            System.out.println(": " + pattern);
            System.out.println();
        }
        System.out.println("Absurdle " + guessedPatterns.size() + "/∞");
        System.out.println();
        printPatterns(guessedPatterns);
    }

    // Prints out the given list of patterns.
    // - List<String> patterns: list of patterns from the game
    public static void printPatterns(List<String> patterns) {
        for (String pattern : patterns) {
            System.out.println(pattern);
        }
    }

    // Returns true if the game is finished, meaning the user guessed the word. Returns
    // false otherwise.
    // - List<String> patterns: list of patterns from the game
    public static boolean isFinished(List<String> patterns) {
        if (patterns.isEmpty()) {
            return false;
        }
        String lastPattern = patterns.get(patterns.size() - 1);
        return !lastPattern.contains("⬜") && !lastPattern.contains("🟨");
    }

    // Loads the contents of a given file Scanner into a List<String> and returns it.
    // - Scanner dictScan: contains file contents
    public static List<String> loadFile(Scanner dictScan) {
        List<String> contents = new ArrayList<>();
        while (dictScan.hasNext()) {
            contents.add(dictScan.next());
        }
        return contents;
    }

    /*
     This method takes in two parameters, one is a list called "contents" which represents the
     words within the dictionary that the user chose. The other parameter is an int "wordLength"
     which represents the amount of letters that the user wants to play absurdle with. The
     purpose of this method is to take the words from the dictionary and exclude all of the words
     whose length is not equal to the length that the user wishes to play. This method returns a
     new list that only includes words whose length are equal to "wordLength". If the user enters
     a word length that is less than 1, than the user will experience an illegalArgumentException
    */
    public static Set<String> pruneDictionary(List<String> contents, int wordLength) {
        if (wordLength < 1){
            throw new IllegalArgumentException("No such thing!");
        }
        Set<String> adjustedDictionary = new TreeSet<String>();
        for (int listIndex = 0; listIndex < contents.size(); listIndex++){
            String currentWord = contents.get(listIndex);
            if (currentWord.length() == wordLength){
                adjustedDictionary.add(currentWord);
            }
        }
        return adjustedDictionary;
    }

    /*
    This method takes in three parameters. The first is a String "guess" that represents the
    users guess for the correct word. The second is a String set "words" that represents the
    possible answers remaining. The last is an int "wordLength" which represents how many 
    characters long the word is. The purpose of this method is to track the users guess, and
    to determine what the most reoccuring pattern is, and shrink the words in the dictionary in
    order to keep the game going for as long as possible. This method returns a string 
    "commonPattern" that represents the pattern that had the most reoccurences based off the
    guess of the user and the words remaining. If the user enters a guess whose length is not 
    equal to the length of the int "wordLength" or if the set "words" is empty, than the user 
    will experience a IllegalArgument exception.
    */
    public static String record(String guess, Set<String> words, int wordLength) {
        if (guess.length() != wordLength || words.isEmpty()){
            throw new IllegalArgumentException("You've ran into an error!");
        }
        Map<String, Set<String>> wordBuckets = new TreeMap<String, Set<String>>();
        assignMap(wordBuckets, words, guess);
            //System.out.println(wordBuckets);
        
        String commonPattern = wordBuckets.keySet().iterator().next();
        //System.out.println(commonPattern);
        for (String pattern : wordBuckets.keySet()){
            //String currentPatternWords = wordBuckets.get(pattern);
            if (wordBuckets.get(pattern).size() > wordBuckets.get(commonPattern).size()){
                commonPattern = pattern; 
            }
        }
        //change the words in the set using iterator 
        words.clear();
        words.addAll(wordBuckets.get(commonPattern));
        return commonPattern;
    }

    /*
    This method takes in three paramets. One is a string to string set map "wordBuckets" whose
    keysets represent a possible pattern for the users guess and whose value is the amount of
    words that fit with the corresponding pattern. The second parameter is a string set "words"
    that represents all possible strings that could fit as an answer for the guess. The last is a
    String "guess" that represents the users guess. The purpose of this method is to translate the
    users guess into patterns with the remaining words from the set "wordBucket" into a map, whose
    key is the pattern and whose value contains every word that fits under the pattern.
    */
    public static void assignMap (Map<String, Set<String>> wordBuckets, Set<String> words,
                                 String guess){
        for (String currentWord : words){
            String pattern = patternFor(currentWord, guess);
            if (!wordBuckets.containsKey(pattern)){
                Set<String> setPattern = new TreeSet<String>();
                setPattern.add(currentWord);
                wordBuckets.put(pattern, setPattern);
            } else {
                wordBuckets.get(pattern).add(currentWord);
            }
        }
    }

    /*
    This method takes in two parameters, one is a string "word" that represents the current word
    in the dictionary that is being compared to the guess. The second is a String "guess" that 
    represents the users guess. The purpose of this method is to go through every possible answer
    for the users guess and create a pattern. If the guess has a letter in the exact spot as the
    "word" than the program will place a green box in the index of the correctly placed letter.
    If the guess contains a letter in the correct word but is not in the right place than the 
    user will recieve a yellow box in the index of the misplaced letter. If the letter in the
    guess doesn't appear at all in the correct word than the letter will be replaced by a gray
    box. The method than returns the pattern for guess compared to the correct word.
    */
    public static String patternFor(String word, String guess) {
        //create guess list
        List<String> userGuess = new LinkedList<String>();
        for (int wordIndex = 0; wordIndex < guess.length(); wordIndex++){
            userGuess.add(wordIndex, "" + guess.charAt(wordIndex));
        }
        //create counter map
        Map<Character, Integer> charCounter = new TreeMap<Character, Integer>();
        for (int letter = 0; letter < word.length(); letter++){
            char currentLetter = word.charAt(letter);
            if (!charCounter.containsKey(currentLetter)){
                charCounter.put(currentLetter, 0);
            }
            charCounter.put(currentLetter, charCounter.get(currentLetter) + 1);
        }
        //assign green squares
        for (int letter = 0; letter < word.length(); letter++){
            char currentLetter = word.charAt(letter);
            if (currentLetter == guess.charAt(letter)){
                userGuess.set(letter, GREEN);
                charCounter.put(currentLetter, charCounter.get(currentLetter) - 1);
            }
        }
        //assign YELLOW
        for (int i = 0; i < word.length(); i++){
            char guessCurrentLetter = guess.charAt(i);
            if (word.contains("" + guessCurrentLetter) && !userGuess.get(i).equals(GREEN) 
                && charCounter.get(guessCurrentLetter) > 0){
                charCounter.put(guessCurrentLetter, charCounter.get(guessCurrentLetter) - 1);
                userGuess.set(i, YELLOW);
            }
        }
        for (int letter = 0; letter < word.length(); letter++){
            if (userGuess.get(letter) != GREEN && userGuess.get(letter) != YELLOW){
                userGuess.set(letter, GRAY);
            }
        }
        String pattern = "";
        for (int patternSquare = 0; patternSquare < userGuess.size(); patternSquare++){
            pattern += userGuess.get(patternSquare);
        }
        return pattern;
    }
}
