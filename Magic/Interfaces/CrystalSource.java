/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Interfaces;

import Reika.ChromatiCraft.Registry.CrystalElement;

public interface CrystalSource extends CrystalTransmitter, LumenTile {

	public int getTransmissionStrength();

	public boolean drain(CrystalElement e, int amt);

	public int getSourcePriority();

	public boolean canTransmitTo(CrystalReceiver te);

}
