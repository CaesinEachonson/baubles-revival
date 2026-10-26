Baubles Revival
===============

This repository is a community continuation of **Baubles**, the bauble slot API for
Minecraft originally created by **Azanor**.

Original work
-------------
    Baubles 1.5.2 (Minecraft 1.12.2)
    Copyright (C) Azanor
    https://github.com/Azanor/Baubles
    Licensed under CC BY-NC-SA 3.0

This project is a derivative work. The original license therefore applies to this
repository as a whole: **Attribution-NonCommercial-ShareAlike 3.0 Unported**
(see `LICENSE`). In short:

* You must credit Azanor as the original author.
* You may not use this work for commercial purposes.
* If you build upon it, you must release your version under the same license.

Changes made in this repository
-------------------------------
Starting point: the 1.12.2 source of Baubles 1.5.2 (31 files, ~1.8k lines).

* Ported to Minecraft 1.21.1 / NeoForge 21.1.x / Java 21.
* Forge capabilities replaced by NeoForge data attachments
  (`AttachmentType`, serialised with a codec, copied on death).
* `SimpleNetworkWrapper` replaced by `CustomPacketPayload` + `StreamCodec`.
* `IGuiHandler`/`GuiContainer` replaced by `MenuType` + `AbstractContainerScreen`.
* Rendering layer rewritten for `PoseStack`; `IRenderBauble` now receives a `PoseStack`.
* Forge configuration replaced by `ModConfigSpec`; commands rewritten with Brigadier.
* 1.12 item/block model and lang assets converted to the 1.13+ formats.
* Fixed the vanilla-inventory switch that made the cursor jump to the screen centre.
* Added a development smoke test (`-Psmoketest`).

Not affiliated with Azanor. This is not an official continuation of Baubles and is not
endorsed by the original author.
