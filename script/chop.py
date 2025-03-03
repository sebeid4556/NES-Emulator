import os
import sys

OPTION_YES = 0
OPTION_NO = 1
OPTION_DEFER = 2

KB = 1024
PRG_BANK_SIZE = 16 * KB
CHR_BANK_SIZE = 8 * KB
INES_HEADER_SIZE = 16
INES_PRG_SIZE_OFFSET = 4
INES_CHR_SIZE_OFFSET = 5

_FILENAME = ""
_PRG_PRESENT = False
_CHR_PRESENT = False
_PRG_SIZE = 0
_CHR_SIZE = 0
_HEADER = []
_PRG = []
_CHR = []

def checkOption(str):
    if 'Y' in str or 'y' in str:
        return OPTION_YES
    if 'N' in str or 'n' in str:
        return OPTION_NO
    else:
        return OPTION_DEFER

def askUser(question):
    while True:
        c = input(question)
        answer = checkOption(c)
        if answer != OPTION_DEFER:
            return True if answer == OPTION_YES else False

def loadROM():
    global _FILENAME
    global _PRG_PRESENT
    global _CHR_PRESENT
    global _PRG_SIZE
    global _CHR_SIZE
    global _HEADER
    global _PRG
    global _CHR
    
    f = open(_FILENAME, "rb")

    print("[+]Selected ROM: %s" % _FILENAME)
    
    _HEADER = f.read(INES_HEADER_SIZE)
    _PRG_SIZE = _HEADER[INES_PRG_SIZE_OFFSET] * PRG_BANK_SIZE
    _CHR_SIZE = _HEADER[INES_CHR_SIZE_OFFSET] * CHR_BANK_SIZE
    
    if _PRG_SIZE == 0:
        _PRG_PRESENT = False
        print("[!]PRG-RAM detected.")
    else:
        _PRG_PRESENT = True
        f.seek(INES_HEADER_SIZE)
        _PRG = f.read(_PRG_SIZE)
        print("[+]PRG-ROM: %s KB (%s bytes)" % (_HEADER[INES_PRG_SIZE_OFFSET] * 16, _PRG_SIZE))
        
    if _CHR_SIZE == 0:
        _CHR_PRESENT = False
        print("[!]CHR-RAM detected.")
    else:
        _CHR_PRESENT = True
        f.seek(INES_HEADER_SIZE + _PRG_SIZE)
        _CHR = f.read(_CHR_SIZE)
        print("[+]CHR-ROM: %s KB (%s bytes)" % (_HEADER[INES_CHR_SIZE_OFFSET] * 8, _CHR_SIZE))
    f.close()

def createNewBinaryFile(filename, data):
    if os.path.exists(filename):        
        if not askUser("[!]" + filename + " already exists. Overwrite? Y/N\t"): return
    f = open(filename, "wb")
    print("[+]Saving to %s..." % filename)
    f.write(data)
    f.close()

def saveAsBinary():
    global _PRG_PRESENT
    global _CHR_PRESENT
    global _PRG
    global _CHR
    
    if _PRG_PRESENT:
        createNewBinaryFile("PRG.BIN", _PRG)
    if _CHR_PRESENT:
        createNewBinaryFile("CHR.BIN", _CHR)

    print("[+]Done.")

def parseArguments():
    global _FILENAME

    argc = len(sys.argv)
    
    if argc < 2:
        if argc == 1:
            print("[+]chop.py [.nes path]\n\t-Extract PRG and CHR data from .ines file")
        else: print("[!]Error: Path to ROM file not provided.")
        sys.exit(1)
    elif argc > 3:
        print("[!]Error: Too many arguments.")
        sys.exit(1)
    filename = sys.argv[1]
    if not os.path.exists(filename):
        print("[!]Error: specified file/path does not exist.")
        sys.exit(1)
    _FILENAME = filename

def main():
    parseArguments()
    loadROM()
    saveAsBinary()

if __name__ == "__main__":
    main()
