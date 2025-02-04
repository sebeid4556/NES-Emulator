# NES-Emulator
Cycle-accurate NES emulator written in java

# Technical
CPU:
- Emulated down to individual clock cycles within an instruction
- Supports full instruction set (including illegal opcodes)

PPU:
- Synchronized w/ system clock and emulated down to cycle precision

The PPU memory space, or VRAM is visualized on the right hand side<br>
- TOP: CHR-ROM (the graphics data in the cartridge)
- BOTTOM: Nametables (the layout for the graphics tiles used to render) 

# Future Enhancements
- Support sprite rendering
- Audio Processing Unit (APU)
- Mapper support
- iNES 2.0 format support
- Switch between CPU & PPU debug

# Screenshots
![alt text](https://github.com/sebeid4556/NES-Emulator/blob/main/screenshot/donkeykong.png?raw=true)
![alt text](https://github.com/sebeid4556/NES-Emulator/blob/main/screenshot/smb.png?raw=true)
![alt text](https://github.com/sebeid4556/NES-Emulator/blob/main/screenshot/nestest.png?raw=true)
![alt text](https://github.com/sebeid4556/NES-Emulator/blob/main/screenshot/test.png?raw=true)
