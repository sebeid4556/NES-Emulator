import os
import sys

def loadROM(path):
    try:
        f = open(path, "rb")
    except:
        print("[!]Error: could not open %s" % path)
        sys.exit(1)
    buffer = f.read()
    f.close()

    if len(buffer) < 16:
        print("[!]Error: selected file does not contain valid iNES header")
        sys.exit(1)
    return buffer

def checkMagic(header):
    if header[:4] != b"NES\x1A":
        return False
    return True

def bit(b, n):
    return (b >> n) & 1

def LINE():
    print("=" * 60)

def printHeader(buffer):
    header = buffer[:16]
    if not checkMagic(header):
        print("[!]Error: file magic does not match")
        sys.exit(1)
    prg_size = header[4]
    chr_size = header[5]
    flags6 = header[6]
    flags7 = header[7]
    sram_size = header[8]
    flags9 = header[9]
    flags10 = header[10]

    mirror = bit(flags6, 0)
    has_sram = bit(flags6, 1) == 1
    has_trainer = bit(flags6, 2) == 1
    alt_nametable = bit(flags6, 3) == 1
    mapper_lo = flags6 & 0b00001111

    vs_unisystem = bit(flags7, 0) == 1
    playchoice = bit(flags7, 1) == 1
    nes2 = (bit(flags7, 2) | (bit(flags7, 3) << 1)) == 0b10
    if nes2:
        print("[!]Error: NES 2.0 not supported")
        sys.exit(1)
    mapper_hi = flags7 & 0b11110000

    mapper = mapper_hi | mapper_lo

    tv_system = bit(flags9, 0)  #(0: NTSC; 1: PAL)

    LINE()
    print("[+]PRG-ROM: %sKB (%s bytes)" % (prg_size * 16, (prg_size * 16 * 1024)))
    print("[+]CHR-ROM: %sKB (%s bytes)" % (chr_size * 8, (chr_size * 8 * 1024)))
    print("[+]Mapper: No. %s" % mapper)
    print("[+]Nametable: ", end='')
    if alt_nametable: print("CUSTOM")
    else:
        if mirror == 0: print("HORIZONTAL")
        else: print("VERTICAL")
    if has_sram: print("[+]Battery-Packed SRAM: Present")
    if has_trainer: print("[+]Trainer: Present")
    if vs_unisystem: print("[+]VS Unisystem: Yes")
    if playchoice: print("[+]PlayChoice-10: Yes")
    print("[+]TV System: ", end='')
    if tv_system == 0: print("NTSC")
    else: print("PAL")
    LINE()

def parseArguments():    
    argc = len(sys.argv)
    
    if argc < 2:
        if argc == 1:
            print("[+]nesinfo.py [.nes path]\n\t-Display info of .ines file")
        else: print("[!]Error: Path to ROM file not provided.")
        sys.exit(1)
    elif argc > 3:
        print("[!]Error: Too many arguments.")
        sys.exit(1)    
    if not os.path.exists(sys.argv[1]):
        print("[!]Error: specified file/path does not exist.")
        sys.exit(1)
    return sys.argv

def main():
    args = parseArguments()
    filename = args[1]
    printHeader(loadROM(filename))

if __name__ == "__main__":
    main()
