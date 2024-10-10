package nes;

public class NES 
{

	public static void main(String[] args) 
	{		
		
		/*byte b = (byte) 0b10000001;
		b = (byte) (Util.UINT8(b) << 1);
		System.out.println(String.format("b = %X", Util.UINT8(b)));
		System.exit(0);*/
		
		Emulator EMULATOR = new Emulator();

		System.out.println("EMULATION START");
		
		EMULATOR.loadROM("rom/test/nestest.nes");
		
		//EMULATOR.loadROM("rom/test/other/FLAME.NES");
		//EMULATOR.loadROM("rom/test/other/BLOCKS.NES");
		//EMULATOR.loadROM("rom/test/other/MOTION.NES");
		//EMULATOR.loadROM("rom/test/other/GENIE.NES");
		//EMULATOR.loadROM("rom/test/other/snow.NES");
		//EMULATOR.loadROM("rom/test/other/BladeBuster.nes");
		//EMULATOR.loadROM("rom/test/other/RasterTest1.nes");
		//EMULATOR.loadROM("rom/test/other/RasterTest3e.nes");
		//EMULATOR.loadROM("rom/test/other/8bitpeoples_-_deadline_console_invitro.nes");		
		
		//EMULATOR.loadROM("rom/test/1-cli_latency.nes");	//FAIL
		//EMULATOR.loadROM("rom/test/1.Branch_Basics.nes");	//PASS
		//EMULATOR.loadROM("rom/test/01-basics.nes");	//PASS
		//EMULATOR.loadROM("rom/test/02-implied.nes");	//PASS
		//EMULATOR.loadROM("rom/test/03-immediate.nes");
		//EMULATOR.loadROM("rom/test/04-zero_page.nes");
		//EMULATOR.loadROM("rom/test/15-brk.nes");	//PASS
		//EMULATOR.loadROM("rom/test/16-special.nes");
		//EMULATOR.loadROM("rom/test/08-ind_x.nes");
		//EMULATOR.loadROM("rom/test/09-ind_y.nes");
		//EMULATOR.loadROM("rom/test/cpu_timing_test.nes");	//CRASH (NEEDS ATTENTION)
		
		//EMULATOR.loadROM("rom/test/ppu/test_ppu_read_buffer.nes");
		//EMULATOR.loadROM("rom/test/ppu/ppu_open_bus.nes");
		//EMULATOR.loadROM("rom/test/ppu/demo_ntsc.nes");
		//EMULATOR.loadROM("rom/test/ppu/01-vbl_basics.nes");	//PASS
		//EMULATOR.loadROM("rom/test/ppu/02-vbl_set_time.nes");	//PASS
		//EMULATOR.loadROM("rom/test/ppu/03-vbl_clear_time.nes");	//PASS
		//EMULATOR.loadROM("rom/test/ppu/04-nmi_control.nes");	//FAIL
		//EMULATOR.loadROM("rom/test/ppu/05-nmi_timing.nes");	//FAIL
		//EMULATOR.loadROM("rom/test/ppu/06-suppression.nes");	//FAIL
		//EMULATOR.loadROM("rom/test/ppu/09-even_odd_frames.nes");	//FAIL
		//EMULATOR.loadROM("rom/test/cpu_dummy_reads.nes");	//PASS
		//EMULATOR.loadROM("rom/test/cpu_dummy_writes_ppumem.nes");	//FAIL
		//EMULATOR.loadROM("rom/test/ppu/palette.nes");	//FAIL (VISUAL ARTIFACTS)
		//EMULATOR.loadROM("rom/test/ppu/full_palette.nes");	//FAIL
		//EMULATOR.loadROM("rom/test/ppu/oam_read.nes");	//PASS
		//EMULATOR.loadROM("rom/test/ppu/oam_stress.nes");	//FAIL
		//EMULATOR.loadROM("rom/test/ppu/scroll.nes");	//???
		
		//EMULATOR.loadROM("rom/game/DonkeyKong.nes");
		//EMULATOR.loadROM("rom/game/SuperMarioBros.nes");
		//EMULATOR.loadROM("rom/game/IceClimber.nes");
		//EMULATOR.loadROM("rom/game/Excitebike.nes");
		
		EMULATOR.run();
		
		System.out.println("EMULATION END");
	}
}
