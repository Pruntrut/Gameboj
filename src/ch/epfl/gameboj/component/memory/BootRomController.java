package ch.epfl.gameboj.component.memory;

import java.util.Objects;

import ch.epfl.gamboj.component.cartridge.Cartridge;
import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;

public final class BootRomController implements Component {
    
    private Cartridge cartridge;
    private boolean bootRomEnabled;
    private Rom bootRom;
    
    public BootRomController(Cartridge cartridge) {
        Objects.requireNonNull(cartridge);
        
        this.cartridge = cartridge;
        bootRom = new Rom(new byte[0]); // TODO : boot program?
        bootRomEnabled = true;
    }
    
    @Override
    public int read(int address) {
        Preconditions.checkBits16(address);
        
        if (AddressMap.BOOT_ROM_START > address && address >= AddressMap.BOOT_ROM_END) {
            return Component.NO_DATA;
        } else if (bootRomEnabled) {
            return bootRom.read(address - AddressMap.BOOT_ROM_START);
        }
        
        return cartridge.read(address); // TODO : address to index conversion necessary?
    }

    @Override
    public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);
        
        if (address == AddressMap.REG_BOOT_ROM_DISABLE) {
            bootRomEnabled = false;
        }
    }

}
