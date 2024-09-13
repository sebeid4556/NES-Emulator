package nes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;

public class CodeViewer extends JPanel{
	
	private final int WIDTH = 256*2;
	private final int HEIGHT = 240*3;
	//private final int WIDTH = 128;
	//private final int HEIGHT = 120;
	
	private final int TEXT_FIELD_WIDTH = WIDTH - 40;
	private final int TEXT_FIELD_HEIGHT = HEIGHT - 40;
	
	private final int NUM_LINES_SHOWN = 20;
	private final int LINE_HIGHLIGHTED = NUM_LINES_SHOWN / 2;
	
	private String SPACE_LEFT = "     ";
	private String TEXT_PAUSE = "PAUSE";
	private String TEXT_PAUSED = "PAUSED";
	private String TEXT_RESUME = "RESUME";
	private String TEXT_STEP = "STEP";
	
	private final int NUM_OPCODES = 256;
	//private String[] OPCODE_ADDRESSING_MODES = new String[NUM_OPCODES];
	
	private String[] OPCODE_NAMES = {
			"BRK", "ORA", "JAM", "SLO", "NOP", "ORA", "ASL", "SLO", "PHP", "ORA", "ASL", "ANC", "NOP", "ORA", "ASL", "SLO", 
			"BPL", "ORA", "JAM", "SLO", "NOP", "ORA", "ASL", "SLO", "CLC", "ORA", "NOP", "SLO", "NOP", "ORA", "ASL", "SLO", 
			"JSR", "AND", "JAM", "RLA", "BIT", "AND", "ROL", "RLA", "PLP", "AND", "ROL", "ANC", "BIT", "AND", "ROL", "RLA", 
			"BMI", "AND", "JAM", "RLA", "NOP", "AND", "ROL", "RLA", "SEC", "AND", "NOP", "RLA", "NOP", "AND", "ROL", "RLA", 
			"RTI", "EOR", "JAM", "SRE", "NOP", "EOR", "LSR", "SRE", "PHA", "EOR", "LSR", "ALR", "JMP", "EOR", "LSR", "SRE", 
			"BVC", "EOR", "JAM", "SRE", "NOP", "EOR", "LSR", "SRE", "CLI", "EOR", "NOP", "SRE", "NOP", "EOR", "LSR", "SRE", 
			"RTS", "ADC", "JAM", "RRA", "NOP", "ADC", "ROR", "RRA", "PLA", "ADC", "ROR", "ARR", "JMP", "ADC", "ROR", "RRA", 
			"BVS", "ADC", "JAM", "RRA", "NOP", "ADC", "ROR", "RRA", "SEI", "ADC", "NOP", "RRA", "NOP", "ADC", "ROR", "RRA", 
			"NOP", "STA", "NOP", "SAX", "STY", "STA", "STX", "SAX", "DEY", "NOP", "TXA", "ANE", "STY", "STA", "STX", "SAX", 	
			"BCC", "STA", "JAM", "SHA", "STY", "STA", "STX", "SAX", "TYA", "STA", "TXS", "TAS", "SHY", "STA", "SHX", "SHA", 
			"LDY", "LDA", "LDX", "LAX", "LDY", "LDA", "LDX", "LAX", "TAY", "LDA", "TAX", "LXA", "LDY", "LDA", "LDX", "LAX", 
			"BCS", "LDA", "JAM", "LAX", "LDY", "LDA", "LDX", "LAX", "CLV", "LDA", "TSX", "LAS", "LDY", "LDA", "LDX", "LAX", 
			"CPY", "CMP", "NOP", "DCP", "CPY", "CMP", "DEC", "DCP", "INY", "CMP", "DEX", "SBX", "CPY", "CMP", "DEC", "DCP", 
			"BNE", "CMP", "JAM", "DCP", "NOP", "CMP", "DEC", "DCP", "CLD", "CMP", "NOP", "DCP", "NOP", "CMP", "DEC", "DCP", 
			"CPX", "SBC", "NOP", "ISC", "CPX", "SBC", "INC", "ISC", "INX", "SBC", "NOP", "SBC", "CPX", "SBC", "INC", "ISC", 
			"BEQ", "SBC", "JAM", "ISC", "NOP", "SBC", "INC", "ISC", "SED", "SBC", "NOP", "ISC", "NOP", "SBC", "INC", "ISC"
	};
	
	private String[] OPCODE_ADDRESSING_MODES = {
			"BRK_IMP", "ORA_X_IND", "JAM", "SLO_X_IND", "NOP_Z", "ORA_Z", "ASL_Z", "SLO_Z", "PHP_IMP", "ORA_IMM", "ASL_A", "ANC_IMM", "NOP_ABS", "ORA_ABS", "ASL_ABS", "SLO_ABS", 
			"BPL_REL", "ORA_IND_Y", "JAM", "SLO_IND_Y", "NOP_Z_X", "ORA_Z_X", "ASL_Z_X", "SLO_Z_X", "CLC_IMP", "ORA_ABS_Y", "NOP_IMP", "SLO_ABS_Y", "NOP_ABS_X", "ORA_ABS_X", "ASL_ABS_X", "SLO_ABS_X", 
			"JSR_ABS", "AND_X_IND", "JAM", "RLA_X_IND", "BIT_Z", "AND_Z", "ROL_Z", "RLA_Z", "PLP_IMP", "AND_IMM", "ROL_A", "ANC_IMM", "BIT_ABS", "AND_ABS", "ROL_ABS", "RLA_ABS", 
			"BMI_REL", "AND_IND_Y", "JAM", "RLA_IND_Y", "NOP_Z_X", "AND_Z_X", "ROL_Z_X", "RLA_Z_X", "SEC_IMP", "AND_ABS_Y", "NOP_IMP", "RLA_ABS_Y", "NOP_ABS_X", "AND_ABS_X", "ROL_ABS_X", "RLA_ABS_X", 
			"RTI_IMP", "EOR_X_IND", "JAM", "SRE_X_IND", "NOP_Z", "EOR_Z", "LSR_Z", "SRE_Z", "PHA_IMP", "EOR_IMM", "LSR_A", "ALR_IMM", "JMP_ABS", "EOR_ABS", "LSR_ABS", "SRE_ABS", 
			"BVC_REL", "EOR_IND_Y", "JAM", "SRE_IND_Y", "NOP_Z_X", "EOR_Z_X", "LSR_Z_X", "SRE_Z_X", "CLI_IMP", "EOR_ABS_Y", "NOP_IMP", "SRE_ABS_Y", "NOP_ABS_X", "EOR_ABS_X", "LSR_ABS_X", "SRE_ABS_X", 
			"RTS_IMP", "ADC_X_IND", "JAM", "RRA_X_IND", "NOP_Z", "ADC_Z", "ROR_Z", "RRA_Z", "PLA_IMP", "ADC_IMM", "ROR_A", "ARR_IMM", "JMP_IND", "ADC_ABS", "ROR_ABS", "RRA_ABS", 
			"BVS_REL", "ADC_IND_Y", "JAM", "RRA_IND_Y", "NOP_Z_X", "ADC_Z_X", "ROR_Z_X", "RRA_Z_X", "SEI_IMP", "ADC_ABS_Y", "NOP_IMP", "RRA_ABS_Y", "NOP_ABS_X", "ADC_ABS_X", "ROR_ABS_X", "RRA_ABS_X", 
			"NOP_IMM", "STA_X_IND", "NOP_IMM", "SAX_X_IND", "STY_Z", "STA_Z", "STX_Z", "SAX_Z", "DEY_IMP", "NOP_IMM", "TXA_IMP", "ANE_IMM", "STY_ABS", "STA_ABS", "STX_ABS", "SAX_ABS", 
			"BCC_REL", "STA_IND_Y", "JAM", "SHA_IND_Y", "STY_Z_X", "STA_Z_X", "STX_Z_Y", "SAX_Z_Y", "TYA_IMP", "STA_ABS_Y", "TXS_IMP", "TAS_ABS_Y", "SHY_ABS_X", "STA_ABS_X", "SHX_ABS_Y", "SHA_ABS_Y", 
			"LDY_IMM", "LDA_X_IND", "LDX_IMM", "LAX_X_IND", "LDY_Z", "LDA_Z", "LDX_Z", "LAX_Z", "TAY_IMP", "LDA_IMM", "TAX_IMP", "LXA_IMM", "LDY_ABS", "LDA_ABS", "LDX_ABS", "LAX_ABS", 
			"BCS_REL", "LDA_IND_Y", "JAM", "LAX_IND_Y", "LDY_Z_X", "LDA_Z_X", "LDX_Z_Y", "LAX_Z_Y", "CLV_IMP", "LDA_ABS_Y", "TSX_IMP", "LAS_ABS_Y", "LDY_ABS_X", "LDA_ABS_X", "LDX_ABS_Y", "LAX_ABS_Y", 
			"CPY_IMM", "CMP_X_IND", "NOP_IMM", "DCP_X_IND", "CPY_Z", "CMP_Z", "DEC_Z", "DCP_Z", "INY_IMP", "CMP_IMM", "DEX_IMP", "SBX_IMM", "CPY_ABS", "CMP_ABS", "DEC_ABS", "DCP_ABS", 
			"BNE_REL", "CMP_IND_Y", "JAM", "DCP_IND_Y", "NOP_Z_X", "CMP_Z_X", "DEC_Z_X", "DCP_Z_X", "CLD_IMP", "CMP_ABS_Y", "NOP_IMP", "DCP_ABS_Y", "NOP_ABS_X", "CMP_ABS_X", "DEC_ABS_X", "DCP_ABS_", 
			"CPX_IMM", "SBC_X_IND", "NOP_IMM", "ISC_X_IND", "CPX_Z", "SBC_Z", "INC_Z", "ISC_Z", "INX_IMP", "SBC_IMM", "NOP_IMP", "USBC_IMM", "CPX_ABS", "SBC_ABS", "INC_ABS", "ISC_ABS", 
			"BEQ_REL", "SBC_IND_Y", "JAM", "ISC_IND_Y", "NOP_Z_X", "SBC_Z_X", "INC_Z_X", "ISC_Z_X", "SED_IMP", "SBC_ABS_Y", "NOP_IMP", "ISC_ABS_Y", "NOP_ABS_X", "SBC_ABS_X", "INC_ABS_X", "ISC_ABS_X"
	};
	
	private String[] OPCODE_ADDRESSING_MODES_MNEMONICS = {
			"BRK", "ORA ($ZZ, X)", "JAM", "SLO ($ZZ, X)", "NOP $ZZ", "ORA $ZZ", "ASL $ZZ", "SLO $ZZ", "PHP", "ORA #", "ASL_A", "ANC #", "NOP $AAAA", "ORA $AAAA", "ASL $AAAA", "SLO $AAAA", 
			"BPL $RR", "ORA ($ZZ), Y", "JAM", "SLO ($ZZ), Y", "NOP $ZZ, X", "ORA $ZZ, X", "ASL $ZZ, X", "SLO $ZZ, X", "CLC", "ORA $AAAA, Y", "NOP", "SLO $AAAA, Y", "NOP $AAAA, X", "ORA $AAAA, X", "ASL $AAAA, X", "SLO $AAAA, X", 
			"JSR $AAAA", "AND ($ZZ, X)", "JAM", "RLA ($ZZ, X)", "BIT $ZZ", "AND $ZZ", "ROL $ZZ", "RLA $ZZ", "PLP", "AND #", "ROL_A", "ANC #", "BIT $AAAA", "AND $AAAA", "ROL $AAAA", "RLA $AAAA", 
			"BMI $RR", "AND ($ZZ), Y", "JAM", "RLA ($ZZ), Y", "NOP $ZZ, X", "AND $ZZ, X", "ROL $ZZ, X", "RLA $ZZ, X", "SEC", "AND $AAAA, Y", "NOP", "RLA $AAAA, Y", "NOP $AAAA, X", "AND $AAAA, X", "ROL $AAAA, X", "RLA $AAAA, X", 
			"RTI", "EOR ($ZZ, X)", "JAM", "SRE ($ZZ, X)", "NOP $ZZ", "EOR $ZZ", "LSR $ZZ", "SRE $ZZ", "PHA", "EOR #", "LSR_A", "ALR #", "JMP $AAAA", "EOR $AAAA", "LSR $AAAA", "SRE $AAAA", 
			"BVC $RR", "EOR ($ZZ), Y", "JAM", "SRE ($ZZ), Y", "NOP $ZZ, X", "EOR $ZZ, X", "LSR $ZZ, X", "SRE $ZZ, X", "CLI", "EOR $AAAA, Y", "NOP", "SRE $AAAA, Y", "NOP $AAAA, X", "EOR $AAAA, X", "LSR $AAAA, X", "SRE $AAAA, X", 
			"RTS", "ADC ($ZZ, X)", "JAM", "RRA ($ZZ, X)", "NOP $ZZ", "ADC $ZZ", "ROR $ZZ", "RRA $ZZ", "PLA", "ADC #", "ROR_A", "ARR #", "JMP ($ZZ)", "ADC $AAAA", "ROR $AAAA", "RRA $AAAA", 
			"BVS $RR", "ADC ($ZZ), Y", "JAM", "RRA ($ZZ), Y", "NOP $ZZ, X", "ADC $ZZ, X", "ROR $ZZ, X", "RRA $ZZ, X", "SEI", "ADC $AAAA, Y", "NOP", "RRA $AAAA, Y", "NOP $AAAA, X", "ADC $AAAA, X", "ROR $AAAA, X", "RRA $AAAA, X", 
			"NOP #", "STA ($ZZ, X)", "NOP #", "SAX ($ZZ, X)", "STY $ZZ", "STA $ZZ", "STX $ZZ", "SAX $ZZ", "DEY", "NOP #", "TXA", "ANE #", "STY $AAAA", "STA $AAAA", "STX $AAAA", "SAX $AAAA", 
			"BCC $RR", "STA ($ZZ), Y", "JAM", "SHA ($ZZ), Y", "STY $ZZ, X", "STA $ZZ, X", "STX $ZZ, Y", "SAX $ZZ, Y", "TYA", "STA $AAAA, Y", "TXS", "TAS $AAAA, Y", "SHY $AAAA, X", "STA $AAAA, X", "SHX $AAAA, Y", "SHA $AAAA, Y", 
			"LDY #", "LDA ($ZZ, X)", "LDX #", "LAX ($ZZ, X)", "LDY $ZZ", "LDA $ZZ", "LDX $ZZ", "LAX $ZZ", "TAY", "LDA #", "TAX", "LXA #", "LDY $AAAA", "LDA $AAAA", "LDX $AAAA", "LAX $AAAA", 
			"BCS $RR", "LDA ($ZZ), Y", "JAM", "LAX ($ZZ), Y", "LDY $ZZ, X", "LDA $ZZ, X", "LDX $ZZ, Y", "LAX $ZZ, Y", "CLV", "LDA $AAAA, Y", "TSX", "LAS $AAAA, Y", "LDY $AAAA, X", "LDA $AAAA, X", "LDX $AAAA, Y", "LAX $AAAA, Y", 
			"CPY #", "CMP ($ZZ, X)", "NOP #", "DCP ($ZZ, X)", "CPY $ZZ", "CMP $ZZ", "DEC $ZZ", "DCP $ZZ", "INY", "CMP #", "DEX", "SBX #", "CPY $AAAA", "CMP $AAAA", "DEC $AAAA", "DCP $AAAA", 
			"BNE $RR", "CMP ($ZZ), Y", "JAM", "DCP ($ZZ), Y", "NOP $ZZ, X", "CMP $ZZ, X", "DEC $ZZ, X", "DCP $ZZ, X", "CLD", "CMP $AAAA, Y", "NOP", "DCP $AAAA, Y", "NOP $AAAA, X", "CMP $AAAA, X", "DEC $AAAA, X", "DCP $AAAA_", 
			"CPX #", "SBC ($ZZ, X)", "NOP #", "ISC ($ZZ, X)", "CPX $ZZ", "SBC $ZZ", "INC $ZZ", "ISC $ZZ", "INX", "SBC #", "NOP", "USBC #", "CPX $AAAA", "SBC $AAAA", "INC $AAAA", "ISC $AAAA", 
			"BEQ $RR", "SBC ($ZZ), Y", "JAM", "ISC ($ZZ), Y", "NOP $ZZ, X", "SBC $ZZ, X", "INC $ZZ, X", "ISC $ZZ, X", "SED", "SBC $AAAA, Y", "NOP", "ISC $AAAA, Y", "NOP $AAAA, X", "SBC $AAAA, X", "INC $AAAA, X", "ISC $AAAA, X"
	};		
	
	private final String ADDRESS_MODE_IMP = "_IMP";
	private final String ADDRESS_MODE_IMM = "_IMM";
	private final String ADDRESS_MODE_Z = "_Z";
	private final String ADDRESS_MODE_Z_X = "_Z_X";
	private final String ADDRESS_MODE_Z_Y = "_Z_Y";
	private final String ADDRESS_MODE_ABS = "_ABS";
	private final String ADDRESS_MODE_ABS_X = "_ABS_X";
	private final String ADDRESS_MODE_ABS_Y = "_ABS_Y";
	private final String ADDRESS_MODE_IND = "_IND";
	private final String ADDRESS_MODE_X_IND = "_X_IND";
	private final String ADDRESS_MODE_IND_Y = "_IND_Y";
	private final String ADDRESS_MODE_REL = "_REL";
	
	private final int IMP_BYTES = 1;
	private final int IMM_BYTES = 2;
	private final int Z_BYTES = 2;
	private final int Z_X_BYTES = 2;
	private final int Z_Y_BYTES = 2;
	private final int ABS_BYTES = 3;
	private final int ABS_X_BYTES = 3;
	private final int ABS_Y_BYTES = 3;
	private final int IND_BYTES = 3;
	private final int X_IND_BYTES = 2;
	private final int IND_Y_BYTES = 2;
	private final int REL_BYTES = 2;
	
	private String labelStr = "";
	
	private JPanel codePane;
	private JPanel buttonPane;
	private JLabel label;
	
	private JButton pauseButton;
	private JButton stepButton;
	
	private Font font;
	
	private int counter = 0;
	
	public CodeViewer(Font font)
	{
		this.font = font;
		
		init();
	}
	
	private void init()
	{
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.GRAY);
		setLayout(new FlowLayout(FlowLayout.LEFT, 16, 16));
		
		codePane = new JPanel();
		codePane.setBackground(Color.BLUE);
		codePane.setPreferredSize(new Dimension(WIDTH - 32, HEIGHT - 32));
		codePane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		add(codePane);
		
		label = new JLabel("UNINITIALIZED");
		label.setBackground(Color.WHITE);
		label.setFont(font);
		codePane.add(label);
		
		buttonPane = new JPanel();
		buttonPane.setBackground(Color.BLACK);
		buttonPane.setPreferredSize(new Dimension(WIDTH - 32, 128));
		//buttonPane.setPreferredSize(new Dimension(256, 64));
		buttonPane.setLayout(new FlowLayout(FlowLayout.LEFT, 16, 16));
		codePane.add(buttonPane);						
		
		pauseButton = new JButton(TEXT_PAUSE);
		pauseButton.setBackground(Color.BLUE);
		pauseButton.setForeground(Color.WHITE);
		pauseButton.setBorderPainted(true);
		pauseButton.setFont(font);
		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{				
				GuiController.pause = !GuiController.pause;
				if(GuiController.pause)
				{
					pauseButton.setBackground(Color.RED);
					pauseButton.setText(TEXT_PAUSED);
				}
				else
				{
					pauseButton.setBackground(Color.BLUE);
					pauseButton.setText(TEXT_PAUSE);
				}
				//System.out.println(String.format("CPU PAUSE STATE: %b", GuiController.pause));
			}
		});
		buttonPane.add(pauseButton);
		
		stepButton = new JButton(TEXT_STEP);
		stepButton.setBackground(Color.BLUE);
		stepButton.setForeground(Color.WHITE);
		stepButton.setBorderPainted(true);
		stepButton.setFont(font);
		buttonPane.add(stepButton);
	}
	
	private int getByteROM(int offset)
	{
		return Util.UINT8(DebugPool.ROM[offset % DebugPool.PRG_UNIT_SIZE]);
	}
	
	private int getShortROM(int offset)
	{
		return Util.UINT16(Util.COMBINE16(
				DebugPool.ROM[offset % DebugPool.PRG_UNIT_SIZE], 
				DebugPool.ROM[(offset + 1) % DebugPool.PRG_UNIT_SIZE]));
	}
	
	private int getOpcode(int offset)
	{
		return getByteROM(offset);
	}
	
	private String getDisassembledInstructionMnemonic(int opcode)
	{
		//System.out.println(String.format("CodeViewer.getDisassembledInstruction(%X): PC = $%X", offset, Util.UINT16(DebugPool.PC)));
		
		//int opcode;
		String opcodeName;
		
		//opcode = Util.UINT8(DebugPool.ROM[offset % DebugPool.PRG_UNIT_SIZE]);		
		opcodeName = OPCODE_ADDRESSING_MODES_MNEMONICS[opcode];
		
		return opcodeName;			
	}
	
	private String getDisassembledInstructionAddressingMode(int opcode)
	{		
		String opcodeName;
		
		opcodeName = OPCODE_ADDRESSING_MODES[opcode];
		
		return opcodeName;			
	}
	
	private String getDisassembledInstructionName(int opcode)
	{		
		String opcodeName;
		
		opcodeName = OPCODE_NAMES[opcode];
		
		return opcodeName;			
	}
	
	private String getDisassembledInstructionParameters(int offset, int opcodeLength, String opcodeName)
	{
		String param = "";		
		String mode;
		int index;		
		
		index = opcodeName.indexOf("_");
		if(index != -1)
		{
			mode = opcodeName.substring(index);
			
			if		(mode.equals(ADDRESS_MODE_IMP)) 	param += "";
			else if (mode.equals(ADDRESS_MODE_IMM)) 	param += String.format("#%02X", getByteROM(offset + 1));
			else if (mode.equals(ADDRESS_MODE_Z))		param += String.format("$%02X", getByteROM(offset + 1));
			else if (mode.equals(ADDRESS_MODE_Z_X)) 	param += String.format("$%02X, X", getByteROM(offset + 1));
			else if (mode.equals(ADDRESS_MODE_Z_Y)) 	param += String.format("$%02X, Y", getByteROM(offset + 1));
			else if (mode.equals(ADDRESS_MODE_ABS)) 	param += String.format("$%04X", getShortROM(offset + 1));
			else if (mode.equals(ADDRESS_MODE_ABS_X)) 	param += String.format("$%04X, X", getShortROM(offset + 1));
			else if (mode.equals(ADDRESS_MODE_ABS_Y)) 	param += String.format("$%04X, Y", getShortROM(offset + 1));
			else if (mode.equals(ADDRESS_MODE_IND)) 	param += String.format("($%04X)", getShortROM(offset + 1));
			else if (mode.equals(ADDRESS_MODE_X_IND)) 	param += String.format("($%02X, X)", getByteROM(offset + 1));
			else if (mode.equals(ADDRESS_MODE_IND_Y)) 	param += String.format("($%02X), Y", getByteROM(offset + 1));
			else if (mode.equals(ADDRESS_MODE_REL))
			{				
				int jump = (int)((byte) (getByteROM(offset + 1) & 0xFF));
				param += String.format("$%04X", 
						(DebugPool.CODE_START + offset + REL_BYTES + jump) );
			}
		}
		return param;
	}
	
	private int getInstructionLength(String opcodeName)
	{
		String mode;
		int index;
		int len = -1;	//error if returns -1
		
		index = opcodeName.indexOf("_");
		if(index != -1)
		{
			mode = opcodeName.substring(index);
			
			if		(mode.equals(ADDRESS_MODE_IMP)) 	len = IMP_BYTES;
			else if (mode.equals(ADDRESS_MODE_IMM)) 	len = IMM_BYTES;
			else if (mode.equals(ADDRESS_MODE_Z)) 		len = Z_BYTES;
			else if (mode.equals(ADDRESS_MODE_Z_X)) 	len = Z_X_BYTES;
			else if (mode.equals(ADDRESS_MODE_Z_Y)) 	len = Z_Y_BYTES;
			else if (mode.equals(ADDRESS_MODE_ABS)) 	len = ABS_BYTES;
			else if (mode.equals(ADDRESS_MODE_ABS_X)) 	len = ABS_X_BYTES;
			else if (mode.equals(ADDRESS_MODE_ABS_Y)) 	len = ABS_Y_BYTES;
			else if (mode.equals(ADDRESS_MODE_IND)) 	len = IND_BYTES;
			else if (mode.equals(ADDRESS_MODE_X_IND)) 	len = X_IND_BYTES;
			else if (mode.equals(ADDRESS_MODE_IND_Y)) 	len = IND_Y_BYTES;		
			else if (mode.equals(ADDRESS_MODE_REL))		len = REL_BYTES;
		}
		return len;
	}
	
	private void updateDisassemblyLines()
	{		
		labelStr = "<html><font color=#ffffff>";
		labelStr += "==== REGISTERS ====<br>";
		labelStr += String.format("- A: $%02X<br>", Util.UINT8(DebugPool.A));
		labelStr += String.format("- X: $%02X<br>", Util.UINT8(DebugPool.X));
		labelStr += String.format("- Y: $%02X<br>", Util.UINT8(DebugPool.Y));
		labelStr += String.format("- P: $%02X<br>", Util.UINT8(DebugPool.P));
		labelStr += String.format("-SP: $%02X<br>", Util.UINT8(DebugPool.SP));
		labelStr += String.format("<span bgcolor=\"yellow\" color=\"black\">-PC: $%04X (OPCODE: %02X)</span><br><br>", Util.UINT16(DebugPool.PC), DebugPool.ir);
		labelStr += "==== &nbsp;&nbsp;CODE &nbsp;&nbsp;&nbsp;====<br>";
		
		int start = 0;
		int offset;
		int highlighted;
		int opcode = 0;
		String opcodeName = "";		
		String opcodeMnemonic = "";
		String param = "";
		int opcodeLength = 0;
		int pc = Util.UINT16(DebugPool.PC);		
		
		if(pc < DebugPool.CODE_START)
		{
			start = 0x0000;
		}
		else
		{
			if(pc < (DebugPool.CODE_START + LINE_HIGHLIGHTED))
			{
				start = 0x0000;
			}
			else if((pc + LINE_HIGHLIGHTED) > 0xFFFF - LINE_HIGHLIGHTED)
			{
				start = 0xFFFF - NUM_LINES_SHOWN;
			}
			else
			{
				start = pc - DebugPool.CODE_START - LINE_HIGHLIGHTED;
			}
		}
		
		/*start = (Util.UINT16(DebugPool.PC) - DebugPool.CODE_START) - LINE_HIGHLIGHTED;
		if(start < 0) 
		{
			if(Util.UINT16(DebugPool.PC) < DebugPool.CODE_START) start = 0;
			else start = Util.UINT16(DebugPool.PC) - DebugPool.CODE_START;
		}
		else if((start + NUM_LINES_SHOWN) >= DebugPool.PRG_UNIT_SIZE) 
		{
			start = DebugPool.PRG_UNIT_SIZE - NUM_LINES_SHOWN;
		}*/
		//if(Util.UINT16(DebugPool.PC) > DebugPool.CODE_START) start = Util.UINT16(DebugPool.PC) - DebugPool.CODE_START;
		
		highlighted = Util.UINT16(DebugPool.PC) - DebugPool.CODE_START;
		
		offset = start;
		//offset = 0x4000;
		for(int line = 0; line < NUM_LINES_SHOWN; line++)
		{
			opcode = getOpcode(offset);
			opcodeName = getDisassembledInstructionAddressingMode(opcode);
			opcodeMnemonic = getDisassembledInstructionMnemonic(opcode);
			param = getDisassembledInstructionParameters(offset, opcodeLength, opcodeName);
			opcodeLength = getInstructionLength(opcodeName);
			if(opcodeLength == -1)
			{
				//System.out.println("ERROR: getInstructionLength() failed to resolve addressing mode.");
			}
			
			if(offset == highlighted) labelStr += "<span bgcolor=\"yellow\" color=\"black\">";
			labelStr += String.format("$%04X|", DebugPool.CODE_START + offset);
			for(int i = 0; i < 3; i++)
			{
				if(i < opcodeLength)
				{
					labelStr += String.format("%02X ", getByteROM(offset + i));
				}
				else
				{
					labelStr += "&nbsp;&nbsp;&nbsp;";				
				}
			}				
			labelStr += "|";
			labelStr += getDisassembledInstructionName(opcode) + " ";
			labelStr += param;
			if(offset == highlighted) labelStr += "</span>";			
			labelStr += "<br>";
			
			offset += opcodeLength;
		}
		labelStr += "</font></html>";
	}			
	
	public void update()
	{		
		updateDisassemblyLines();
		System.out.println(counter);
		label.setText(labelStr);	
		counter++;
	}
}
