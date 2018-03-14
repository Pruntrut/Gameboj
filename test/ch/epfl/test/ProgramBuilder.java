package ch.epfl.test;

import java.util.ArrayList;

import ch.epfl.gameboj.Bus;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.cpu.Opcode;
import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;

public class ProgramBuilder {
    
    private ArrayList<Integer> program;
    private ArrayList<Integer> memory;
    private Cpu cpu;
    public static final int PREFIX = 0xCB;
    private static final int MEMORY_INDEX = 0x20;
    private int totalCycles = 0;
    
    public ProgramBuilder() {
        program = new ArrayList<>();
        memory = new ArrayList<>();
    }
    
    private boolean isPrefixed(Opcode op) {
        return op.kind == Opcode.Kind.PREFIXED;
    }
    
    public void execOp(Opcode op) {
        if (isPrefixed(op)) {
            program.add(PREFIX);
        }
        
        program.add(op.encoding);
        totalCycles += op.cycles;
    }
    
    public void execOpAnd8(Opcode op, int value) {
        Preconditions.checkBits8(value);
        execOp(op);
        program.add(value);
    }
    
    public void execOpAnd16(Opcode op, int value) {
        Preconditions.checkBits16(value);
        execOp(op);
        addInt(Bits.clip(8, value));
        addInt(Bits.extract(value, 8, 8));
    }
    
    public void addInt(int value) {
        Preconditions.checkBits8(value);
        program.add(value);
    }
    
    private int[] build() {
        int initialSize = program.size();
        for (int i = 0; i < MEMORY_INDEX - initialSize; i++) {
            program.add(0);
        }
        program.addAll(memory);
        
        int[] data = new int[program.size()];
        for (int i = 0; i < program.size(); i++) {
            data[i] = program.get(i);
        }
        
        return data;
    }
    
    public void storeInt(int value) {
        memory.add(value);
    }
    
    public int getMemoryAddress(int address) {
        return MEMORY_INDEX + address;
    }
    
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
    
    public void run(int cycle) {
        int[] data = build();
        cpu = newCpu(data);
        runCpu(cpu, cycle);
    }
    
    public void run() {
        run(totalCycles);
    }
    
    public int[] getResult() {
        return cpu._testGetPcSpAFBCDEHL();
    }
}
