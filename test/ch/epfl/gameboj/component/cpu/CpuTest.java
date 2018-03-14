package ch.epfl.gameboj.component.cpu;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.epfl.gameboj.Bus;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;

class CpuTest {
    
    private Cpu newCpu(int[] program) {
        Ram ram = new Ram(program.length);
        RamController rc = new RamController(ram, 0);
        Bus bus = new Bus();        
        Cpu cpu = new Cpu();
        
        // Fill ram
        for (int i = 0; i < program.length; i++) {
            rc.write(i, program[i]);
        }
        
        rc.attachTo(bus);
        cpu.attachTo(bus);
        
        return cpu;
    }
    
    private void runCpu(Cpu cpu, int numberOfCycles) {
        for (long i = 0; i < numberOfCycles; i++) {
            cpu.cycle(i);
        }
    }
    
    private Cpu newCpu() {
        return newCpu(new int[0]);
    }
    

    @Test
    void attachToDoesNotThrowException() {
        Bus b = new Bus();
        Cpu c = newCpu();
        
        c.attachTo(b);
    }
    
    @Test
    void readReturnsNoData() {
        assertEquals(Component.NO_DATA, newCpu().read(0xFFFF));
        assertEquals(Component.NO_DATA, newCpu().read(0x0000));
        assertEquals(Component.NO_DATA, newCpu().read(0xF0F0));
    }
    
    @Test
    void writeDoesNotThrowException() {
        newCpu().write(0x00, 0xFF);
    }
    
    @Test
    void initalRegistersAreAllZero() {
        int[] expected = {0,0,0,0,0,0,0,0,0,0};
        Cpu cpu = newCpu();
        
        assertArrayEquals(expected, cpu._testGetPcSpAFBCDEHL());
    }
    
    @Test
    void testNOP() {
        int[] program = { Opcode.NOP.encoding };
        Cpu cpu = newCpu(program);
        int[] expected = {1,0,0,0,0,0,0,0,0,0};
        
        cpu.cycle(0);
        assertArrayEquals(expected, cpu._testGetPcSpAFBCDEHL());
    }
    
    @Test
    void testLD_R8_HLR() {        
        int[] program = {
                Opcode.LD_HL_N16.encoding,      // 0: LD HL nn
                0x04,                           // 1:
                0x00,                           // 2: address 0x0004
                Opcode.LD_A_HLR.encoding,       // 3: LD A HL
                0x13                            // 4: 
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[2]);
    }
    
    @Test
    void testLD_A_HLRU_Increment() {
        int[] program = {
                Opcode.LD_HL_N16.encoding,      // 0: LD HL nn
                0x04,                           // 1:
                0x00,                           // 2: address 0x0004
                Opcode.LD_A_HLRI.encoding,      // 3: LD A [HL+] HL = 0x0005
                0x13                            // 4: HL = 0x0005
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0x05, cpu._testGetPcSpAFBCDEHL()[9]);
    }
    
    @Test
    void testLD_A_HLRU_Decrement() {
        int[] program = {
                Opcode.LD_HL_N16.encoding,      // 0: LD HL nn
                0x04,                           // 1:
                0x00,                           // 2: address 0x0004
                Opcode.LD_A_HLRD.encoding,      // 3: LD A [HL+] HL = 0x0005
                0x13                            // 4: HL = 0x0005
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0x03, cpu._testGetPcSpAFBCDEHL()[9]);
    }
    
    @Test
    void testLD_A_N8R() {
    	    int[] program = new int[0xFFFF];
    	
    	    program[0] = Opcode.LD_A_N8R.encoding;
    	    program[1] = 0x04;
    	    program[0xFF00 + 0x04] = 0x13;
    	
    	    Cpu cpu = newCpu(program);
        runCpu(cpu, 5);
         
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[2]);
    }
    
    @Test
    void testLD_A_CR() {
        int[] program = new int[0xFFFF];
        
        program[0] = Opcode.LD_C_N8.encoding;
        program[1] = 0x04;
        program[2] = Opcode.LD_A_CR.encoding;
        program[0xFF00 + 0x04] = 0x13;
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, 5);
        
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[2]);
    }
    
    @Test
    void testLD_A_N16R() {
        int[] program = {
                Opcode.LD_A_N16R.encoding,
                0x03,
                0x00,
                0x013
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[2]);
    }
    
    @Test
    void testLD_A_BCR() {
        int[] program = {
                Opcode.LD_BC_N16.encoding,
                0x04,
                0x00,
                Opcode.LD_A_BCR.encoding,
                0x13
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[2]);
        
    }
    
    @Test
    void testLD_A_DER() {
        int[] program = {
                Opcode.LD_BC_N16.encoding,
                0x04,
                0x00,
                Opcode.LD_A_BCR.encoding,
                0x13
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[2]);
    }
    
    @Test
    void testLD_R8_N8() {
        int[] program = {
                Opcode.LD_E_N8.encoding,
                0x13,
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[7]);
    }
    
    @Test
    void testLD_R16SP_N16() {
        int[] program = {
                Opcode.LD_DE_N16.encoding,
                0x13,
                0x14
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[7]);
        assertEquals(0x14, cpu._testGetPcSpAFBCDEHL()[6]);
    }

    @Test
    void testLD_R16SP_N16WithSPAsRegister() {
        int[] program = {
                Opcode.LD_SP_N16.encoding,
                0x13,
                0x14
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x1413, cpu._testGetPcSpAFBCDEHL()[1]);
    }
    
    @Test
    void testPOP_R16() {
        int[] program = new int[0xFF00];
        program[0] = Opcode.LD_SP_N16.encoding;
        program[1] = 0x04;
        program[2] = 0x05;
        program[3] = Opcode.POP_BC.encoding;
        program[0x0504] = 0x14;
        program[0x0505] = 0x13;
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, 5);
        
        assertEquals(0x14, cpu._testGetPcSpAFBCDEHL()[5]);
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[4]);
        assertEquals(0x0504 + 2, cpu._testGetPcSpAFBCDEHL()[1]);
    }
    
    @Test
    void testLD_HLR_R8() {
        int[] program = {
        		Opcode.LD_C_N8.encoding,		// 0
        		0x13,							// 1
        		Opcode.LD_HL_N16.encoding,		
        		0x09,
        		0x00,
        		Opcode.LD_HLR_C.encoding,		// BUS[HL] = C
        		Opcode.LD_A_N16R.encoding,		// A = BUS[0x0009]
        		0x09,
        		0x00,
        		0x00							// Target
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[2]);
    }
    
    @Test
    void testHLRU_ADecrement() {
    	int[] program = {
    			Opcode.LD_A_N8.encoding,	// A = 0x13
    			0x13,
    			Opcode.LD_HL_N16.encoding,	// HL = 0x0009
    			0x09,
    			0x00,
    			Opcode.LD_HLRD_A.encoding,	// BUS[HL] = A ; HL -= 1;
    			Opcode.LD_A_N16R.encoding,	// A = BUS[0x0009]
    			0x09,
    			0x00,
    			0x00						// Target
    	};
    	
    	Cpu cpu = newCpu(program);
    	runCpu(cpu, program.length);
    	
    	assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[2]);
    	assertEquals(0x08, cpu._testGetPcSpAFBCDEHL()[9]);
    	assertEquals(0x00, cpu._testGetPcSpAFBCDEHL()[8]);
    }
    
    @Test
    void testHLRU_AIncrement() {
    	int[] program = {
    			Opcode.LD_A_N8.encoding,	// A = 0x13
    			0x13,
    			Opcode.LD_HL_N16.encoding,	// HL = 0x0009
    			0x09,
    			0x00,
    			Opcode.LD_HLRI_A.encoding,	// BUS[HL] = A ; HL += 1;
    			Opcode.LD_A_N16R.encoding,	// A = BUS[0x0009]
    			0x09,
    			0x00,
    			0x00						// Target
    	};
    	
    	Cpu cpu = newCpu(program);
    	runCpu(cpu, program.length);
    	
    	assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[2]);
    	assertEquals(0x0A, cpu._testGetPcSpAFBCDEHL()[9]);
    	assertEquals(0x00, cpu._testGetPcSpAFBCDEHL()[8]);
    }
    
    @Test
    void testLD_N8R_A() {
    	int[] program = new int[0xFFFF];
    	program[0] = Opcode.LD_A_N8.encoding;
    	program[1] = 0x13;
    	program[2] = Opcode.LD_N8R_A.encoding;
    	program[3] = 0x04;
    	program[4] = Opcode.LD_A_N8.encoding;
    	program[5] = 0x01;
    	program[6] = Opcode.LD_A_N16R.encoding;
    	program[7] = 0x04; 
    	program[8] = 0xFF;
    	
    	Cpu cpu = newCpu(program);
    	runCpu(cpu, 10);
    	
    	assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[2]);
	}
    
    @Test
    void testLD_CR_A() {
        int[] program = new int[0xFFFF];
        program[0] = Opcode.LD_A_N8.encoding;
        program[1] = 0x13;
        program[2] = Opcode.LD_C_N8.encoding;
        program[3] = 0x04;
        program[4] = Opcode.LD_CR_A.encoding;
        program[5] = Opcode.LD_A_N8.encoding;
        program[6] = 0x01;
        program[7] = Opcode.LD_A_N16R.encoding;
        program[8] = 0x04; 
        program[9] = 0xFF;
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, 10);
        
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[2]);
    }
    
    @Test
    void testLD_N16R_A() {
        int[] program = new int[0xFFFF];
        program[0] = Opcode.LD_A_N8.encoding;
        program[1] = 0x13;
        program[2] = Opcode.LD_N16R_A.encoding;
        program[3] = 0x04;
        program[4] = 0xFF;
        program[5] = Opcode.LD_A_N8.encoding;
        program[6] = 0x01;
        program[7] = Opcode.LD_A_N16R.encoding;
        program[8] = 0x04; 
        program[9] = 0xFF;
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, 10);
        
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[2]);
    }
    
    @Test
    void testLD_BCR_A() {
        int[] program = new int[0xFFFF];
        program[0] = Opcode.LD_A_N8.encoding;
        program[1] = 0x13;
        program[2] = Opcode.LD_BC_N16.encoding;
        program[3] = 0x04;
        program[4] = 0xFF;
        program[5] = Opcode.LD_BCR_A.encoding;
        program[6] = Opcode.LD_A_N8.encoding;
        program[7] = 0x01;
        program[8] = Opcode.LD_A_N16R.encoding;
        program[9] = 0x04; 
        program[10] = 0xFF;
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, 11);
        
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[2]);
    }
    
    @Test
    void testLD_DER_A() {
        int[] program = new int[0xFFFF];
        program[0] = Opcode.LD_A_N8.encoding;
        program[1] = 0x13;
        program[2] = Opcode.LD_DE_N16.encoding;
        program[3] = 0x04;
        program[4] = 0xFF;
        program[5] = Opcode.LD_DER_A.encoding;
        program[6] = Opcode.LD_A_N8.encoding;
        program[7] = 0x01;
        program[8] = Opcode.LD_A_N16R.encoding;
        program[9] = 0x04; 
        program[10] = 0xFF;
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, 11);
        
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[2]);
    }
    
    @Test
    void testLD_HLR_N8() {
        int[] program = {
                Opcode.LD_HL_N16.encoding,  // HL = 0x0005
                0x08,
                0x00,
                Opcode.LD_HLR_N8.encoding,  // BUS[HL] = 0x13
                0x13,                       
                Opcode.LD_A_N16R.encoding,  // A = BUS[0x0005]
                0x08,
                0x00,
                0x01                        // Target
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[2]);
    }
    
    @Test
    void testLD_N16R_SP() {
        int[] program = {
                Opcode.LD_SP_N16.encoding,  // SP = 0x1413
                0x13,
                0x14,
                Opcode.LD_N16R_SP.encoding, // BUS[0x000D] = SP
                0x0D,
                0x00,
                Opcode.LD_A_N16R.encoding,  // A = BUS[0x000D]
                0x0D,
                0x00,
                Opcode.LD_B_A.encoding,     // B = A
                Opcode.LD_A_N16R.encoding,  // A = BUS[0x000E]
                0x0E,
                0x00,
                0x01,                       // Target 1
                0x02                        // Target 2
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x14, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[4]);
    }
    
    @Test
    void testPUSH_R16() {
        int[] program = {
                Opcode.LD_SP_N16.encoding,
                0x0C,
                0x00,
                Opcode.LD_DE_N16.encoding,
                0x13,
                0x14,
                Opcode.PUSH_DE.encoding,
                Opcode.LD_A_N16R.encoding,
                0x0A,
                0x00,
                0x00
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x0A, cpu._testGetPcSpAFBCDEHL()[1]);
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[2]);
    }
    
    @Test
    void testLD_R8_R8() {
        int[] program = {
                Opcode.LD_B_N8.encoding,
                0x13,
                Opcode.LD_L_B.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[9]);
    }
    
    
    @Test
    void testLD_SP_HL() {
        int[] program = {
                Opcode.LD_HL_N16.encoding,
                0x13,
                0x14,
                Opcode.LD_SP_HL.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x1413, cpu._testGetPcSpAFBCDEHL()[1]);
    }
    
/* ADD/INC 8-bits  tests */
    
    @Test
    void testADD_A_R8WithSimpleValue() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x13,
                Opcode.LD_B_N8.encoding,
                0x14,
                Opcode.ADD_A_B.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x27, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b0000_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testADD_A_R8WithOverflowAndZero() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0xFF,
                Opcode.LD_B_N8.encoding,
                0x01,
                Opcode.ADD_A_B.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x00, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b1011_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testADD_A_R8WithOverflow() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0xFF,
                Opcode.LD_B_N8.encoding,
                0x02,
                Opcode.ADD_A_B.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x01, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b0011_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testADD_A_N8WithSimpleValue() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x13,
                Opcode.ADD_A_N8.encoding,
                0x14
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x27, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b0000_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testADD_A_N8WithOverflow() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0xFF,
                Opcode.ADD_A_N8.encoding,
                0x01
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x00, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b1011_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testADD_A_HLRWithSimpleValue() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x13,
                Opcode.LD_HL_N16.encoding,
                0x06,
                0x00,
                Opcode.ADD_A_HLR.encoding,
                0x14
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x27, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b0000_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testADD_A_HLRWithOverflow() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0xFF,
                Opcode.LD_HL_N16.encoding,
                0x06,
                0x00,
                Opcode.ADD_A_HLR.encoding,
                0x01
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x00, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b1011_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testADC_A_R8WithSimpleValue() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x13,
                Opcode.LD_B_N8.encoding,
                0x14,
                Opcode.SCF.encoding,
                Opcode.ADC_A_B.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x28, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b0000_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testADC_A_R8WithOverflowAndZero() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0xFF,
                Opcode.LD_B_N8.encoding,
                0x00,
                Opcode.SCF.encoding,
                Opcode.ADC_A_B.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x00, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b1011_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testADC_A_R8WithOverflow() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0xFF,
                Opcode.LD_B_N8.encoding,
                0x01,
                Opcode.SCF.encoding,
                Opcode.ADC_A_B.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x01, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b0011_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testADC_A_N8WithSimpleValue() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x13,
                Opcode.SCF.encoding,
                Opcode.ADC_A_N8.encoding,
                0x14
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x28, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b0000_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testADC_A_N8WithOverflow() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0xFF,
                Opcode.SCF.encoding,
                Opcode.ADC_A_N8.encoding,
                0x01
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x01, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b0011_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testADC_A_HLRWithSimpleValue() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x13,
                Opcode.LD_HL_N16.encoding,
                0x07,
                0x00,
                Opcode.SCF.encoding,
                Opcode.ADC_A_HLR.encoding,
                0x14
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x28, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b0000_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testADC_A_HLRWithOverflow() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0xFF,
                Opcode.LD_HL_N16.encoding,
                0x07,
                0x00,
                Opcode.SCF.encoding,
                Opcode.ADC_A_HLR.encoding,
                0x01
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x01, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b0011_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testINC_R8WithSimpleValue() {
        int[] program = {
                Opcode.LD_B_N8.encoding,
                0x13,
                Opcode.SCF.encoding,
                Opcode.INC_B.encoding,
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x14, cpu._testGetPcSpAFBCDEHL()[4]);
        assertEquals(0b0001_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testINC_R8WithOverflow() {
        int[] program = {
                Opcode.LD_C_N8.encoding,
                0xFF,
                Opcode.SCF.encoding,
                Opcode.INC_C.encoding,
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x00, cpu._testGetPcSpAFBCDEHL()[5]);
        assertEquals(0b1011_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testINC_HLRWithSimpleValue() {
        int[] program = {
                Opcode.LD_HL_N16.encoding,
                0x08,
                0x00,
                Opcode.SCF.encoding,
                Opcode.INC_HLR.encoding,
                Opcode.LD_A_N16R.encoding,
                0x08,
                0x00,
                0x13
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x14, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b0001_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testINC_HLRWithOverflow() {
        int[] program = {
                Opcode.LD_HL_N16.encoding,
                0x08,
                0x00,
                Opcode.SCF.encoding,
                Opcode.INC_HLR.encoding,
                Opcode.LD_A_N16R.encoding,
                0x08,
                0x00,
                0xFF
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x00, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b1011_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testADD_HL_R16SP() {
        int[] program = {
                Opcode.LD_HL_N16.encoding,
                0x13,
                0x14,
                Opcode.LD_DE_N16.encoding,
                0x15,
                0x16,
                Opcode.ADD_HL_DE.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x28, cpu._testGetPcSpAFBCDEHL()[9]);
        assertEquals(0x2A, cpu._testGetPcSpAFBCDEHL()[8]);
        assertEquals(0b0000_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testADD_HL_R16SPCorrectAFSP() {
        int[] program = {
                Opcode.LD_HL_N16.encoding,
                0x13,
                0x14,
                Opcode.LD_SP_N16.encoding,
                0x15,
                0x16,
                Opcode.ADD_HL_SP.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x28, cpu._testGetPcSpAFBCDEHL()[9]);
        assertEquals(0x2A, cpu._testGetPcSpAFBCDEHL()[8]);
        assertEquals(0b0000_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testADD_HL_R16SPCorrectHCFlags() {
        int[] program = {
                Opcode.LD_HL_N16.encoding,
                0x15,
                0x1B,
                Opcode.LD_SP_N16.encoding,
                0x17,
                0x1A,
                Opcode.ADD_HL_SP.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x35, cpu._testGetPcSpAFBCDEHL()[8]);
        assertEquals(0x2C, cpu._testGetPcSpAFBCDEHL()[9]);
        assertEquals(0b0010_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testINC_R16SP() {
        int[] program = {
                Opcode.LD_DE_N16.encoding,
                0xFF,
                0x00,
                Opcode.SCF.encoding,
                Opcode.INC_DE.encoding,
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x00, cpu._testGetPcSpAFBCDEHL()[7]);
        assertEquals(0x01, cpu._testGetPcSpAFBCDEHL()[6]);
        assertEquals(0b0001_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testINC_R16SPCorrectAFSP() {
        int[] program = {
                Opcode.LD_SP_N16.encoding,
                0xFF,
                0x00,
                Opcode.SCF.encoding,
                Opcode.INC_SP.encoding,
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x0100, cpu._testGetPcSpAFBCDEHL()[1]);
        assertEquals(0b0001_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testLD_HLSP_S8OnSPRegister() {
        int[] program = {
                Opcode.LD_SP_N16.encoding,
                0x13,
                0x14,       //  0x1413
                Opcode.ADD_SP_N.encoding,
                0xED        // -0x13 = 0x1400
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x1400, cpu._testGetPcSpAFBCDEHL()[1]);
        assertEquals(0b0011_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testLD_HLSP_S8OnHLRegister() {
        int[] program = {
                Opcode.LD_SP_N16.encoding,
                0x13,
                0x14,       //  0x1413
                Opcode.LD_HL_SP_N8.encoding,
                0xED        // -0x13 = 0x1400
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x00, cpu._testGetPcSpAFBCDEHL()[9]);
        assertEquals(0x14, cpu._testGetPcSpAFBCDEHL()[8]);
        assertEquals(0b0011_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }

    
    /* SUB/DEC tests */
    
    @Test
    void testSUB_A_R8() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x14,
                Opcode.LD_B_N8.encoding,
                0x05,
                Opcode.SUB_A_B.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x0F, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b01100000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testSUB_A_R8WithUnderflow() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x02,
                Opcode.LD_B_N8.encoding,
                0x04,
                Opcode.SUB_A_B.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0xFE, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b01110000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testSUB_A_R8WithCarry() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x14,
                Opcode.LD_B_N8.encoding,
                0x05,
                Opcode.SCF.encoding,
                Opcode.SBC_A_B.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x0E, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b01100000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testSUB_A_R8WhenZero() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x14,
                Opcode.LD_B_N8.encoding,
                0x14,
                Opcode.SBC_A_B.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x00, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b11000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testSUB_A_N8() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x14,
                Opcode.SUB_A_N8.encoding,
                0x05
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x0F, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b01100000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testSUB_A_N8WithCarry() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x14,
                Opcode.SCF.encoding,
                Opcode.SBC_A_N8.encoding,
                0x05
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x0E, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b01100000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testSUB_A_N8WithUnderflow() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x14,
                Opcode.SUB_A_N8.encoding,
                0x016
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0xFE, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b01110000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testSUB_A_HLR() {
        int[] program = {
                Opcode.LD_HL_N16.encoding,
                0x06,
                0x00,
                Opcode.LD_A_N8.encoding,
                0x14,
                Opcode.SUB_A_HLR.encoding,
                0x05
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x0F, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b01100000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testSUB_A_HLRWithUnderFlow() {
        int[] program = {
                Opcode.LD_HL_N16.encoding,
                0x06,
                0x00,
                Opcode.LD_A_N8.encoding,
                0x14,
                Opcode.SUB_A_HLR.encoding,
                0x05
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x0F, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b01100000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testSUB_A_HLRWithCarry() {
        int[] program = {
                Opcode.LD_HL_N16.encoding,
                0x07,
                0x00,
                Opcode.LD_A_N8.encoding,
                0x14,
                Opcode.SCF.encoding,
                Opcode.SBC_A_HLR.encoding,
                0x05
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x0E, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b01100000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testDEC_R8() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x14,
                Opcode.DEC_A.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b01000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testDEC_R8WithUnderflow() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x00,
                Opcode.DEC_A.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0xFF, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b01100000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testDEC_HLR() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x14,
                Opcode.DEC_A.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b01000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testDEC_HLRWithUnderflow() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x00,
                Opcode.DEC_A.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0xFF, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b01100000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testCP_A_R8() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x08,
                Opcode.LD_B_N8.encoding,
                0x06,
                Opcode.CP_A_B.encoding,
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b01000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testCP_A_R8WithUnderFlow() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x06,
                Opcode.LD_B_N8.encoding,
                0x08,
                Opcode.CP_A_B.encoding,
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b01110000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testCP_A_R8WhenZero() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x08,
                Opcode.LD_B_N8.encoding,
                0x08,
                Opcode.CP_A_B.encoding,
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b11000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testCP_A_R8WithCarryInLSB() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x12,
                Opcode.LD_B_N8.encoding,
                0x06,
                Opcode.CP_A_B.encoding,
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b01100000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testCP_A_N8() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x08,
                Opcode.CP_A_N8.encoding,
                0x06
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b01000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testCP_A_N8WithUnderFlow() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x06,
                Opcode.CP_A_N8.encoding,
                0x08
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b01110000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testCP_A_N8WhenZero() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x08,
                Opcode.CP_A_N8.encoding,
                0x08
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b11000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testCP_A_N8WithCarryInLSB() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0x12,
                Opcode.CP_A_N8.encoding,
                0x06
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b01100000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testCP_A_HLR() {
        int[] program = {
                Opcode.LD_HL_N16.encoding,
                0x06,
                0x00,
                Opcode.LD_A_N8.encoding,
                0x09,
                Opcode.CP_A_HLR.encoding,
                0x07
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b01000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testCP_A_HLRWithUnderFlow() {
        int[] program = {
                Opcode.LD_HL_N16.encoding,
                0x06,
                0x00,
                Opcode.LD_A_N8.encoding,
                0x07,
                Opcode.CP_A_HLR.encoding,
                0x09
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b01110000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testCP_A_HLRWhenZero() {
        int[] program = {
                Opcode.LD_HL_N16.encoding,
                0x06,
                0x00,
                Opcode.LD_A_N8.encoding,
                0x09,
                Opcode.CP_A_HLR.encoding,
                0x09
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b11000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testCP_A_HLRWithCarryInLSB() {
        int[] program = {
                Opcode.LD_HL_N16.encoding,
                0x06,
                0x00,
                Opcode.LD_A_N8.encoding,
                0x12,
                Opcode.CP_A_HLR.encoding,
                0x07
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b01100000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testDEC_R16SP() {
        int[] program = {
                Opcode.LD_BC_N16.encoding,
                0x04,
                0xEE,
                Opcode.DEC_BC.encoding,
                
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x03, cpu._testGetPcSpAFBCDEHL()[5]);
        assertEquals(0xEE, cpu._testGetPcSpAFBCDEHL()[4]);
        assertEquals(0b00000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testDEC_R16SPWithUnderFlow() {
        int[] program = {
                Opcode.LD_BC_N16.encoding,
                0x00,
                0x00,
                Opcode.DEC_BC.encoding,
                
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0xFF, cpu._testGetPcSpAFBCDEHL()[4]);
        assertEquals(0xFF, cpu._testGetPcSpAFBCDEHL()[5]);
        assertEquals(0b00000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testDEC_16SPWithCarryInLSB() {
        int[] program = {
                Opcode.LD_BC_N16.encoding,
                0x00,
                0x01,
                Opcode.DEC_BC.encoding,
                
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0xFF, cpu._testGetPcSpAFBCDEHL()[5]);
        assertEquals(0x00, cpu._testGetPcSpAFBCDEHL()[4]);
        assertEquals(0b00000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testDEC_16SPWithAF() {
        int[] program = {
                Opcode.LD_SP_N16.encoding,
                0x14,
                0x00,
                Opcode.DEC_SP.encoding,
                
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0x13, cpu._testGetPcSpAFBCDEHL()[1]);
        assertEquals(0b00000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    /* AND, OR, XOR tests */
    
    @Test
    void testAND_A_N8() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0b00110011,
                Opcode.AND_A_N8.encoding,
                0b11110000
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b00110000, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b00100000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testAND_A_N8WhenResultIsZero() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0b00000011,
                Opcode.AND_A_N8.encoding,
                0b11110000
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b00000000, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b10100000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testAND_A_R8() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0b00110011,
                Opcode.LD_B_N8.encoding,
                0b11110000,
                Opcode.AND_A_B.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b00110000, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b00100000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testAND_A_HLR() {
        int[] program = {
                Opcode.LD_HL_N16.encoding,
                0x06,
                0x00,
                Opcode.LD_A_N8.encoding,
                0b00110011,
                Opcode.AND_A_HLR.encoding,
                0b11110000
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b00110000, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b00100000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testOR_A_N8() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0b00110011,
                Opcode.OR_A_N8.encoding,
                0b11110000
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b11110011, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b00000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testOR_A_N8WhenResultIsZero() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0b00000000,
                Opcode.OR_A_N8.encoding,
                0b00000000
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b00000000, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b10000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testOR_A_R8() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0b00110011,
                Opcode.LD_B_N8.encoding,
                0b11110000,
                Opcode.OR_A_B.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b11110011, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b00000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testOR_A_HLR() {
        int[] program = {
                Opcode.LD_HL_N16.encoding,
                0x06,
                0x00,
                Opcode.LD_A_N8.encoding,
                0b00110011,
                Opcode.OR_A_HLR.encoding,
                0b11110000
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b11110011, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b00000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testXOR_A_N8() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0b00110011,
                Opcode.XOR_A_N8.encoding,
                0b11110000
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b11000011, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b00000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testXOR_A_N8WhenResultIsZero() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0b11110000,
                Opcode.XOR_A_N8.encoding,
                0b11110000
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b00000000, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b10000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testXOR_A_R8() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0b00110011,
                Opcode.LD_B_N8.encoding,
                0b11110000,
                Opcode.XOR_A_B.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b11000011, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b00000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testXOR_A_HLR() {
        int[] program = {
                Opcode.LD_HL_N16.encoding,
                0x06,
                0x00,
                Opcode.LD_A_N8.encoding,
                0b00110011,
                Opcode.XOR_A_HLR.encoding,
                0b11110000
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b11000011, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b00000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testCPL() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0b00110011,
                Opcode.CPL.encoding,
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b11001100, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b01100000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    /* Rotations tests */
    
    @Test
    void testRLC_A() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0b10110011,
                Opcode.RLC_A.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b01100111, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b00010000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testRRC_A() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0b10110011,
                Opcode.RRC_A.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b11011001, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b00010000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testRRC_AZeroShiftedOut() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0b10110010,
                Opcode.RRC_A.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b01011001, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b00000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testRRC_R8ZFlagAndCFlag() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0b01010000,
                Opcode.RRC_A.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b00101000, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b00000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testRL_A() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0b10110011,
                Opcode.RL_A.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b01100110, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b00010000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testRR_AWithCFZero() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0b10110010,
                Opcode.RR_A.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b01011001, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b00000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testRR_AWithCFOne() {
        int[] program = {
                Opcode.LD_A_N8.encoding,
                0b10110010,
                Opcode.SCF.encoding,
                Opcode.RR_A.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b11011001, cpu._testGetPcSpAFBCDEHL()[2]);
        assertEquals(0b00000000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testRLC_R8() {
        int[] program = {
                Opcode.LD_C_N8.encoding,
                0b10110011,
                0xCB,
                Opcode.RLC_C.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b01100111, cpu._testGetPcSpAFBCDEHL()[5]);
        assertEquals(0b00010000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testRRC_R8() {
        int[] program = {
                Opcode.LD_D_N8.encoding,
                0b10110011,
                0xCB,
                Opcode.RRC_D.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b11011001, cpu._testGetPcSpAFBCDEHL()[6]);
        assertEquals(0b00010000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    
     
    /* Test for carry flag methods */
    
    @Test
    void testSCF() {
        int[] program = {
                Opcode.SCF.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b0001_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testCCFZeroToOne() {
        int[] program = {
                Opcode.CCF.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b0001_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
    
    @Test
    void testCCFOneToZero() {
        int[] program = {
                Opcode.SCF.encoding,
                Opcode.CCF.encoding
        };
        
        Cpu cpu = newCpu(program);
        runCpu(cpu, program.length);
        
        assertEquals(0b0000_0000, cpu._testGetPcSpAFBCDEHL()[3]);
    }
}

