package ch.epfl.gameboj.component.cartridge;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.memory.Rom;

/**
 * Represents a cartridge that can be inserted in the gameboy
 * 
 * @author Sylvain Kuchen (282380)
 * @author Luca Bataillard (282152)
 */
public final class Cartridge implements Component {
    
    private final Component memoryBank;
    public static final int MB_TYPE_ADDRESS = 0x147;
    public static final int RAM_SIZE_ADDRESS = 0x149;
    public static final int MB_TYPE_0 = 0;
    public static final int[] MB_TYPE_1 = {1, 2, 3};
    
    private static final int[] RAM_SIZES = {0x0000, 0x0800, 0x2000, 0x8000};
    
    private Cartridge(Component memoryBank) {
        this.memoryBank = memoryBank;
    }
    
    /**
     * Creates a new cartridge from a rom file
     * @param romFile : the path to the file
     * @throws IOException if size of read bytes is not the size of the bytes
     * @throws IllegalArgumentException if MB_TYPE_ADDRESS is not 0 (not an MBC0 rom file)
     * @throws IllegalArgumentException if the number of bytes is not MBC0.ROM_SIZE
     * @return a cartridge with the data of the rom file
     */
    public static Cartridge ofFile(File romFile) throws IOException {
        try(InputStream stream = new FileInputStream(romFile)) {
            
            byte[] data = new byte[(int) romFile.length()];
            
            int streamSize = stream.read(data);
            
            if (streamSize != data.length) {
                throw new IOException();
            }
            
            Rom rom = new Rom(data);
            
            if(data[MB_TYPE_ADDRESS] == MB_TYPE_0) {
                return new Cartridge(new MBC0(rom));
            } else if (Arrays.binarySearch(MB_TYPE_1, data[MB_TYPE_ADDRESS]) >= 0) {
                int ramSize = RAM_SIZES[data[RAM_SIZE_ADDRESS]];
                return new Cartridge(new MBC1(rom, ramSize));
            } else {
                throw new IllegalArgumentException("Cartridge type is not supported : type " + data[MB_TYPE_ADDRESS]);
            }
        }
    }        

    /**
     * Reads at given address
     * @param address: 16 bits
     * @throws IllegalArgumentException if address not 16 bits
     * @return the read data
     */
    @Override
    public int read(int address) {
        Preconditions.checkBits16(address);
        
        return memoryBank.read(address); 
    }

    /**
     * Writes to memory bank
     * @param address : 16 bits
     * @param data : 8 bits
     * @throws IllegalArgumentException if data not 8 bits or address not 16 bits
     */
    @Override
    public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);
        
        memoryBank.write(address, data);
    }
}
