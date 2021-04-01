//Import packages needed to run program  
import java.io.*;
import java.util.Scanner;
import java.util.PriorityQueue;
import java.util.IllegalFormatException;

//Main Class Huffman 
public class Huffman_Coding {
    
    //Method to number of characters supportd in the alphabet based on what is being encoded, this can vary depending on language etc. 
    private static final int ALPHABET_SIZE = 100000000;

    //Generating the frequency of the characters 
    private static int[] generateFrequency(String text) { 
        int[] freq = new int[ALPHABET_SIZE]; 
        // Loop through each of the characters inside the text 
        for (char character : text.toCharArray()) { 
            //Frequency of the character gets incremented 
                freq[character]++; 
            }
            // Return the Frequency Array 
        return freq;
    }

    //Method to get the Codes for each letter and add into an array 
    private static void getCodes(Node tree, String code, String[] codes){
        // If the node is a leaf node 
        if(tree.isLeaf()){ 
        // Add the code to the array 
        codes[tree.getLetter()] = code; 
            return; 
        }

        //If moving left of the binary tree adding a 0 
        getCodes(tree.left, code + '0', codes); 
        //If moving rifhr of the binary tree adding a 1 
        getCodes(tree.right, code + '1', codes); 
    }

    //Read File Method 
    private static String readFile(String fileName) {
    // Create a new StringBuffer object in order to allow for mutability 
    StringBuffer text = new StringBuffer(); 
    try {
    //New File entered to read
    File toRead = new File(fileName); 
    // New Scanner object created for reading 
    Scanner reader = new Scanner(toRead); 
    // While loop set up to ensure reading through entire text 
    while (reader.hasNextLine()){ 
                // Ensures white space is not comprimised 
                text.append(reader.nextLine() + "\n"); 
            }
        } catch (FileNotFoundException e){
            // If there is an invalid path given error is thrown. 
            System.out.println("Error! Invalid File Path. Please enter it again");
            System.exit(0);
        }
        return text.toString(); 
    }


    //Write to File Method 
    private static void writeToNewFile(String text, String filename){
        try {
            // New file to write to
            File towrite = new File(filename);
            FileWriter writer = new FileWriter(towrite);
            //The input text is written to the file 
            writer.write(text); 
            writer.close();
            System.out.println("File has been decompressed successfully");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unsuccessful decompression, please try again");

        }
    }

    //Creating the Byte Array 
    private static byte[] generateByteArray(String data) {
        //Creating a new byte array and dividing the length by 7, one less than the standard of 8
        byte[] byteArray = new byte[data.length()/7];
        //For loop to traverse through and increment i
        for(int i = 0; i < byteArray.length; i++) {
            byteArray[i] = (byte)Integer.parseInt(data.substring(i * 7, i * 7 + 7), 2);
        }
        return  byteArray;
    }

    //Method to Write Bits to the File 
    private static void bitsToFile(String encodedData, String filename){
        byte[] byteArray = generateByteArray(encodedData);
        try(FileOutputStream out = new FileOutputStream(filename)){
            out.write(byteArray); 
            System.out.println("File has been compressed successfully"); 
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unsuccessful compression, please try again");
        }
    }


    //Method to Read Bytes from the File 
    private static String readBytes(String filename){
        // New String Buffer Object to ensure mutability 
        StringBuffer byteString = new StringBuffer();
        try {
            File file = new File(filename);
            // New Byte Array 
            byte[] bytes = new byte[(int) file.length()]; 
            FileInputStream input = new FileInputStream(file);
            //Reads the file into the Byte Array 
            input.read(bytes); 
            input.close(); 
            for(int i = 0; i < bytes.length; i++){ 
                int indexedCharacter = bytes[i];
                String stringRepresentation = String.format("%7s", Integer.toBinaryString(indexedCharacter)).replace(' ', '0');
                byteString.append(stringRepresentation); 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteString.toString(); 
    }

    //Method to Build the Huffman Tree 
    private static Node buildHuffmanTree(int[] freq) {
        //Counter of the Number of Nodes 
        int nodeNumber = 0;
        //Creates a priority queue of nodes (most frequent to least frequent)
        PriorityQueue<Node> nodes = new PriorityQueue<>();
        for(int i = 0; i < ALPHABET_SIZE; i++){
            if(!(freq[i] == 0)){ 
                //Create new node if character appears 
                Node character = new Node(freq[i], (char) i); 
                //Add to priority queue
                nodes.add(character);
                nodeNumber += 1;
            }
        }
        //While loop for nodes to be attached to a parent node 
        while (nodes.size() > 1){ 
            try {
                //Removing the least frequently appearing by adding to the queue 
                Node leftNode = nodes.poll(); 
                Node rightNode = nodes.poll(); 
                //Creates a parent node
                Node parentNode = new Node(leftNode.getFrequency() + rightNode.getFrequency(), '\u0000', leftNode, rightNode);
                nodes.add(parentNode); 
                nodeNumber += 1;
            }catch (NullPointerException e){
                System.exit(0);
            }
        }
        //Returns the root node 
        return nodes.poll(); 
    }

    //Method to Compress the Text 
    private static String compress(String text, String[] codes){
        StringBuffer compressedText = new StringBuffer();
        for(char character : text.toCharArray()){
            try{
                if(!(codes[character] == null) || !(codes[character].equals(""))){
                    compressedText.append(codes[character]); 
                }
            }catch (IndexOutOfBoundsException e){
                continue; 
            } catch (NullPointerException ignore){}
        }
        return compressedText.toString();
    }

    // Method to Decompress the Text 
    private static String decompress(String compressedText, Node root){
        StringBuffer decompressedText = new StringBuffer(); 
        //Retrieve the current bit 
        Node bit = root; 
        for(int i = 0; i < compressedText.length(); i++){
            // If 0, move left in the Binary Tree 
            if(compressedText.charAt(i) == '0'){ 
                bit = bit.getLeft(); 
            // If 1, move right in the Binary Tree 
            } else if(compressedText.charAt(i) == '1') { 
                bit = bit.getRight(); 
            // Incorrect huffman tree if neither 0 or 1 
            } else {
                throw new IllegalArgumentException("Unexpected Error! Please try again ");
            }
            //If the current bit has been reached as leaf, retrieve character 
            if (bit.isLeaf()){ 
                decompressedText.append(bit.getLetter()); 
                bit = root; 
            }
        }
        System.out.println("Text decoded successfully");
        //Returns the string buffer 
        return decompressedText.toString() + "\n"; 
    }

    //Class to Set and Get the Nodes Nodes 
    public static class Node implements Comparable<Node>{ 
        private int frequency; 
        private Node left; 
        private Node right; 
        private char letter; 

        Node(int frequency, char letter) { 
            this.frequency = frequency;
            this.letter = letter;
            this.left = null;
            this.right = null;
        }

        // Child Nodes
        Node(int frequency, char letter, Node left, Node right) { 
            this.frequency = frequency;
            this.letter = letter;
            this.left = left;
            this.right = right;
        }

        //Set Left 
        public void setLeft(Node left) {
            this.left = left;
        }

        //Set Right
        public void setRight(Node right) {
            this.right = right;
        }


        //Get Left 
        public Node getLeft() {
            return left;
        }

        //Get Right
        public Node getRight() {
            return right;
        }

        //Get Letter
        public char getLetter() {
            return letter;
        }

        //Get Frequency 
        public int getFrequency() {
            return frequency;
        }

        //Check is Node is a leaf 
        public boolean isLeaf() {
            return (this.right == null && this.left == null); 
        }

        @Override
        //Compare frequencie of Nodes 
        public int compareTo(Node that) { 
            int comparison = Integer.compare(this.frequency, that.frequency); 
            if(comparison != 0){
                return comparison;
            }else{ 
                return Integer.compare(this.letter, that.letter); 
            }
        }

    }


    public static void main(String[] args) throws IOException {

        //User to input the File Path 
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the File Path:");
        String filePath = scan.nextLine();
        System.out.println("Enter the Compress File Path:");
        String compressedFilePath = scan.nextLine();
        System.out.println("Enter the DeCompress File Path:");
        String decompressedFilePath = scan.nextLine();

        //Creating the Huffman Tree 
        String[] codes = new String[ALPHABET_SIZE]; 
        String fileData = readFile(filePath); 
        int[] freq = generateFrequency(fileData); 
        Node tree = buildHuffmanTree(freq); 
        getCodes(tree, "", codes); 

        //Compressing 
        String encodedData = compress(fileData, codes); 
        bitsToFile(encodedData, compressedFilePath); 

        //Decompressing 
        String bitsInCompressedFile = readBytes(compressedFilePath); 
        String decodedData = decompress(bitsInCompressedFile, tree); 
        writeToNewFile(decodedData, decompressedFilePath); 
    }
}











