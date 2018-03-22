package ch.epfl.gameboj.component.cartridge;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
    
    private MBC0 memoryBank;
    
    private Cartridge(byte[] data) {
        memoryBank = new MBC0(new Rom(data));
    }
    
    public static Cartridge ofFile(File romFile) throws IOException {
        try(InputStream file = new BufferedInputStream(new FileInputStream(romFile))) {
            
            byte[] data = new byte[MBC0.ROM_SIZE];
            byte nextByte;
            int index = 0;
            
            while ((nextByte = (byte) file.read()) != -1) {
                data[index] = nextByte;
                index++;
            }
            
            if (data[0x147] != 0) {
                throw new IllegalArgumentException();
            }
            
            return new Cartridge(data);
        }
        
        
    }

    @Override
    public int read(int address) {
        Preconditions.checkBits16(address);
        // TODO : implement method correctly
        return memoryBank.read(address); 
    }

    @Override
    public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);
        
        memoryBank.write(address, data);
    }

}
