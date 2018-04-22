package ch.epfl.gameboj.component.lcd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.epfl.gameboj.bits.BitVector;

class LcdImageLineTest {

	private static final int SIZE = 32;
	private static final BitVector MSB = buildMsb();
	private static final BitVector LSB = buildLsb();
	private static final BitVector OP = buildOpactiy();
	
	@Test
	void constructorFailsForInvalidArguments() {
		BitVector zero = new BitVector(SIZE);
		
		assertThrows(NullPointerException.class, () -> 
				new LcdImageLine(null, zero, zero));
		assertThrows(NullPointerException.class, () -> 
				new LcdImageLine(zero, null, zero));
		assertThrows(NullPointerException.class, () -> 
				new LcdImageLine(zero, zero, null));
		
		BitVector size1 = new BitVector(SIZE*2);
		BitVector size2 = new BitVector(SIZE);
		
		assertThrows(IllegalArgumentException.class, () -> 
				new LcdImageLine(size1, size2, zero));
	}
	
	@Test
	void constructorInitalizesCorrectly() {
		LcdImageLine l = new LcdImageLine(MSB, LSB, OP);
		assertEquals(MSB.size(), l.size());
		assertEquals(MSB, l.msb());
		assertEquals(LSB, l.lsb());
		assertEquals(OP, l.opacity());
	}
	
	@Test
	void shiftWorks() {
		LcdImageLine l = new LcdImageLine(MSB, LSB, OP);

		for(int i = 0; i < SIZE; i++) {
			assertEquals(MSB.shift(i), l.shift(i).msb());
			assertEquals(LSB.shift(i), l.shift(i).lsb());
			assertEquals(OP.shift(i), l.shift(i).opacity());
		}
	}
	
	@Test
	void extractWrappedWorks() {
		LcdImageLine l = new LcdImageLine(MSB, LSB, OP);
		
		for (int i = -SIZE; i < 2*SIZE; i++) {
			assertEquals(MSB.extractWrapped(i, SIZE), l.extractWrapped(i, SIZE).msb());
			assertEquals(LSB.extractWrapped(i, SIZE), l.extractWrapped(i, SIZE).lsb());
			assertEquals(OP.extractWrapped(i, SIZE), l.extractWrapped(i, SIZE).opacity());
		}
	}
	
	@Test
	void joinWorksForComplexLines() {
		BitVector one = new BitVector(SIZE, true);
		LcdImageLine oneLine = new LcdImageLine(one, one, one);
		LcdImageLine main = new LcdImageLine(MSB, LSB, OP);
		LcdImageLine combined = main.join(oneLine, 21);
		
		assertEquals(Integer.toBinaryString(0xFFEF32AA), combined.msb().toString());
		assertEquals(Integer.toBinaryString(0xFFEF71DD), combined.lsb().toString());
		assertEquals(Integer.toBinaryString(0xFFE03F66), combined.opacity().toString());
		
		assertEquals(oneLine, main.join(oneLine, 0));
		assertEquals(main, main.join(oneLine, SIZE));
	}
	
	@Test
	void mapColorsWorksForKnownValues() {
	    int[] msbBytes = {0b00000011,  0b11110000, 0b00111111, 0b00000011,  0b11110000, 0b00111111, 
	                       0b00000011,  0b11110000, 0b00111111, 0b00000011,  0b11110000, 0b00111111};
	    int[] lsbBytes = {0b00011100, 0b01110001,  0b11000111, 0b00011100, 0b01110001,  0b11000111,
	                       0b00011100, 0b01110001,  0b11000111, 0b00011100, 0b01110001,  0b11000111};
	    
	    BitVector msb = buildVector(msbBytes);
	    BitVector lsb = buildVector(lsbBytes);
	    BitVector opacity = new BitVector(96, true);
	    
	    LcdImageLine line = new LcdImageLine(msb, lsb, opacity);
	    LcdImageLine line2 = line.mapColors(0b00011011);
	    
	    assertEquals(line.msb().not(), line2.msb());
	    assertEquals(line.lsb().not(), line2.lsb());
	    
	}
	

	private static BitVector buildMsb() {
		BitVector.Builder bvb = new BitVector.Builder(32);
		return bvb.setByte(0, 0xAA).setByte(1, 0x32).setByte(2, 0x0F).setByte(3, 0x69).build();
	}
	
	private static BitVector buildLsb() {
		BitVector.Builder bvb = new BitVector.Builder(32);
		return bvb.setByte(0, 0xDD).setByte(1, 0x71).setByte(2, 0x0F).setByte(3, 0x68).build();
	}
	
	private static BitVector buildOpactiy() {
		BitVector.Builder bvb = new BitVector.Builder(32);
		return bvb.setByte(0, 0x66).setByte(1, 0x3F).setByte(2, 0x00).setByte(3, 0x0F).build();
	}
	
	private static BitVector buildVector(int[] bytes) {
	    BitVector.Builder bvb = new BitVector.Builder(bytes.length * 8);
	    
	    for (int i = 0; i < bytes.length; i++) {
	        bvb.setByte(i, bytes[i]);
	    }
	    
	    return bvb.build();
	}
	
	
}
