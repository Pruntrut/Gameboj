package ch.epfl.gameboj.component.cpu;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ch.epfl.test.ProgramBuilder;

class CpuTestStep5 {

    @Test
    void fibonacci() {
        int[] program = new int[] {
            0x31, 0xFF, 0xFF, 0x3E,
            0x0B, 0xCD, 0x0A, 0x00,
            0x76, 0x00, 0xFE, 0x02,
            0xD8, 0xC5, 0x3D, 0x47,
            0xCD, 0x0A, 0x00, 0x4F,
            0x78, 0x3D, 0xCD, 0x0A,
            0x00, 0x81, 0xC1, 0xC9,
        };
        
        ProgramBuilder pb = new ProgramBuilder(program, 200000000);
        pb.run();
        
        //assertEquals(8, pb.getResult()[0]);
        assertEquals(89, pb.getResult()[5]);
    }
    
    @Test
    void testJP_HL() {
        ProgramBuilder pb = new ProgramBuilder();
        pb.execOpAnd16(Opcode.LD_HL_N16, 0x0006);
        pb.execOp(Opcode.JP_HL);
        pb.execOpAnd8(Opcode.LD_B_N8, 0xDD);
        pb.execOpAnd8(Opcode.LD_A_N8, 0xEE);
        pb.run();
        
        assertEquals(0xEE, pb.getResult()[2]);
        assertEquals(0x00, pb.getResult()[4]);
    }
    
    @Test
    void testJP_N16() {
        ProgramBuilder pb = new ProgramBuilder();
        pb.execOpAnd16(Opcode.JP_N16, 0x0005);
        pb.execOpAnd8(Opcode.LD_B_N8, 0xDD);
        pb.execOpAnd8(Opcode.LD_A_N8, 0xEE);
        pb.run();
        
        assertEquals(0xEE, pb.getResult()[2]);
        assertEquals(0x00, pb.getResult()[4]);
    }
    
    @Test
    void testJP_NZ_N16() {
        ProgramBuilder pb = new ProgramBuilder();        
        pb.execOpAnd8(Opcode.LD_A_N8, 0x0F);
        pb.execOpAnd8(Opcode.CP_A_N8, 0x0E);
        pb.execOpAnd16(Opcode.JP_NZ_N16, 0x0009);
        pb.execOpAnd8(Opcode.LD_B_N8, 0xDD);
        pb.execOpAnd8(Opcode.LD_C_N8, 0xEE);
        pb.run();
        
        assertEquals(0x00, pb.getResult()[4]);
        assertEquals(0xEE, pb.getResult()[5]);
    }
    
    @Test
    void testJP_Z_N16() {
        ProgramBuilder pb = new ProgramBuilder();        
        pb.execOpAnd8(Opcode.LD_A_N8, 0x0F);
        pb.execOpAnd8(Opcode.CP_A_N8, 0x0F);
        pb.execOpAnd16(Opcode.JP_Z_N16, 0x0009);
        pb.execOpAnd8(Opcode.LD_B_N8, 0xDD);
        pb.execOpAnd8(Opcode.LD_C_N8, 0xEE);
        pb.run();
        
        assertEquals(0x00, pb.getResult()[4]);
        assertEquals(0xEE, pb.getResult()[5]);
    }
    
    @Test
    void testJP_NC_N16() {
        ProgramBuilder pb = new ProgramBuilder();        
        pb.execOpAnd8(Opcode.LD_A_N8, 0x0F);
        pb.execOpAnd8(Opcode.CP_A_N8, 0x0E);
        pb.execOpAnd16(Opcode.JP_NZ_N16, 0x0009);
        pb.execOpAnd8(Opcode.LD_B_N8, 0xDD);
        pb.execOpAnd8(Opcode.LD_C_N8, 0xEE);
        pb.run();
        
        assertEquals(0x00, pb.getResult()[4]);
        assertEquals(0xEE, pb.getResult()[5]);
    }
    
    @Test
    void testJP_C_N16() {
        ProgramBuilder pb = new ProgramBuilder();        
        pb.execOpAnd8(Opcode.LD_A_N8, 0x0E);
        pb.execOpAnd8(Opcode.CP_A_N8, 0x0F);
        pb.execOpAnd16(Opcode.JP_NZ_N16, 0x0009);
        pb.execOpAnd8(Opcode.LD_B_N8, 0xDD);
        pb.execOpAnd8(Opcode.LD_C_N8, 0xEE);
        pb.run();
        
        assertEquals(0x00, pb.getResult()[4]);
        assertEquals(0xEE, pb.getResult()[5]);
    }
    
    @Test
    void testJR_E8() {
        ProgramBuilder pb = new ProgramBuilder();
        pb.execOpAnd8(Opcode.JR_E8, 0x02);
        pb.execOpAnd8(Opcode.LD_B_N8, 0xDD);
        pb.execOpAnd8(Opcode.LD_C_N8, 0xEE);
        pb.run();
        
        assertEquals(0x00, pb.getResult()[4]);
        assertEquals(0xEE, pb.getResult()[5]);
    }
    
    @Test
    void testJR_E8WithNegativeValue() {
        ProgramBuilder pb = new ProgramBuilder();
        pb.execOpAnd8(Opcode.JR_E8, 0x04);
        pb.execOpAnd8(Opcode.LD_B_N8, 0xDD);
        pb.execOpAnd8(Opcode.LD_C_N8, 0xEE);
        pb.execOpAnd8(Opcode.JR_E8, 0xFC);
        pb.run();
        
        assertEquals(0x00, pb.getResult()[4]);
        assertEquals(0xEE, pb.getResult()[5]);
    }
    
    @Test
    void testJR_NZ_N16() {
        ProgramBuilder pb = new ProgramBuilder();        
        pb.execOpAnd8(Opcode.LD_A_N8, 0x0F);
        pb.execOpAnd8(Opcode.CP_A_N8, 0x0E);
        pb.execOpAnd8(Opcode.JR_NZ_E8, 0x02);
        pb.execOpAnd8(Opcode.LD_B_N8, 0xDD);
        pb.execOpAnd8(Opcode.LD_C_N8, 0xEE);
        pb.run();
        
        assertEquals(0x00, pb.getResult()[4]);
        assertEquals(0xEE, pb.getResult()[5]);
    }
    
    @Test
    void testJR_Z_N16() {
        ProgramBuilder pb = new ProgramBuilder();        
        pb.execOpAnd8(Opcode.LD_A_N8, 0x0F);
        pb.execOpAnd8(Opcode.CP_A_N8, 0x0F);
        pb.execOpAnd8(Opcode.JR_Z_E8, 0x02);
        pb.execOpAnd8(Opcode.LD_B_N8, 0xDD);
        pb.execOpAnd8(Opcode.LD_C_N8, 0xEE);
        pb.run();
        
        assertEquals(0x00, pb.getResult()[4]);
        assertEquals(0xEE, pb.getResult()[5]);
    }
    
    @Test
    void testJR_NC_N16() {
        ProgramBuilder pb = new ProgramBuilder();        
        pb.execOpAnd8(Opcode.LD_A_N8, 0x0F);
        pb.execOpAnd8(Opcode.CP_A_N8, 0x0E);
        pb.execOpAnd8(Opcode.JR_NZ_E8, 0x02);
        pb.execOpAnd8(Opcode.LD_B_N8, 0xDD);
        pb.execOpAnd8(Opcode.LD_C_N8, 0xEE);
        pb.run();
        
        assertEquals(0x00, pb.getResult()[4]);
        assertEquals(0xEE, pb.getResult()[5]);
    }
    
    @Test
    void testJR_C_N16() {
        ProgramBuilder pb = new ProgramBuilder();        
        pb.execOpAnd8(Opcode.LD_A_N8, 0x0E);
        pb.execOpAnd8(Opcode.CP_A_N8, 0x0F);
        pb.execOpAnd8(Opcode.JR_NZ_E8, 0x02);
        pb.execOpAnd8(Opcode.LD_B_N8, 0xDD);
        pb.execOpAnd8(Opcode.LD_C_N8, 0xEE);
        pb.run();
        
        assertEquals(0x00, pb.getResult()[4]);
        assertEquals(0xEE, pb.getResult()[5]);
    }
    
    @Test
    void testCALL_N16() {
        ProgramBuilder pb = new ProgramBuilder();
        pb.execOpAnd16(Opcode.LD_SP_N16, 0xFFFD);
        pb.execOpAnd16(Opcode.CALL_N16, 0x08);
        pb.execOpAnd8(Opcode.LD_B_N8, 0xDD);
        pb.execOpAnd8(Opcode.LD_C_N8, 0xEE);
        
        assertEquals(0x00, pb.getResult()[4]);
        assertEquals(0xEE, pb.getResult()[5]);
        assertEquals(0x06, pb.get16BitsFromBus(0xFFFD));
    }
}
