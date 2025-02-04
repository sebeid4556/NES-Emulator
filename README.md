# NES-Emulator
Cycle-accurate NES emulator written in Java
![image](https://github.com/sebeid4556/NES-Emulator/blob/c0ff935341d7d75f061b70cd06f1200ea1e65b47/screenshot/all.png)

# Technical
CPU:
- Emulated down to individual clock cycles within an instruction
- Supports full instruction set (including illegal opcodes)
- Passes nestest.nes (100% instruction accuracy)

PPU:
- Synchronized w/ system clock and emulated down to cycle precision

# Debuggers
The debuggers are displayed on the right-hand side of the app window.
![alt text](https://github.com/sebeid4556/NES-Emulator/blob/ce552b7dde0234ea5ae9b95bed1541f61c8e5c71/screenshot/cpu-demo.gif)</br>
The CPU Debugger shows the code dump, registers, flags, etc.. It also allows you to pause execution at any point and step through individual instructions.</br></br>
![alt text](https://github.com/sebeid4556/NES-Emulator/blob/ce552b7dde0234ea5ae9b95bed1541f61c8e5c71/screenshot/ppu-demo.gif)
The PPU Debugger shows the visual representation of VRAM and CHR-ROM:</br>
- TOP: CHR-ROM (the graphics data in the cartridge)
- BOTTOM: Nametables (the layout for the background tiles)

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
