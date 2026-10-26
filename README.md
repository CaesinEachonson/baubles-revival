# Baubles Revival

A community port of [Baubles](https://github.com/Azanor/Baubles) — the seven-slot bauble
inventory and bauble API — from Minecraft **1.12.2 / Forge** to **1.21.1 / NeoForge**.

The goal is to keep the original API surface (`baubles.api.*`) intact so that mods that
were written against Baubles 1.5.2 — most notably Thaumcraft 6 — can be brought forward
without redesigning their item code.

> **Not an official continuation.** Baubles was created by Azanor, who stopped developing
> it. This repository is an independent port and is not endorsed by the original author.
> Please read [NOTICE.md](NOTICE.md) and [LICENSE](LICENSE).

[中文说明](#中文说明)

---

## Status

| | |
|---|---|
| Target | Minecraft 1.21.1, NeoForge 21.1.x, Java 21 |
| Build | `gradlew build` → `build/libs/Baubles-Revival-<version>.jar` |
| Dedicated server | Loads cleanly, registries and attachments initialise |
| Client | Inventory screen opens/closes, slots work, baubles render on the player |
| Automated check | `-Psmoketest` server self-test covers slots, slot typing, equip, command |

What works today:

* Seven bauble slots (amulet, belt, head, body, charm, two rings) stored on the player.
* Expanded inventory screen with the original 2x2 crafting grid, armour and offhand slots.
* `B` key (configurable keybind) and the inventory button toggle between the vanilla
  inventory and the bauble inventory.
* Items implementing `IBauble` are wearable; `onWornTick`, `onEquipped`, `onUnequipped`,
  `canEquip`, `canUnequip` and `willAutoSync` are called like in the original.
* Baubles are saved with the player, copied on death/dimension change, and dropped on
  death unless `keepInventory` is enabled.
* Equipped baubles are synced to nearby clients and rendered on the player when the item
  implements `IRenderBauble`.
* `/baubles view|clear <player> [slot]` (aliases `/baub` and `/bau`).
* Optional config: `client.renderBaubles`.

Not implemented yet:

* The recipe book integration of the variant inventory screen.
* Baubles' own `IBaublesItemHandler` extension points beyond what the 1.5.2 API exposed.
* The 1.12 "Baubles" creative tab (the example ring is in Tools & Utilities instead).

## Requirements

* Java 21
* Minecraft 1.21.1 with NeoForge 21.1.x

## Build

```bash
./gradlew build
```

The jar ends up in `build/libs/`.

### `-PecjWorkaround`

On some machines the javac based recompile of the decompiled Minecraft sources inside
NeoFormRuntime fails with a `package-info.java: null` diagnostic, and javac is then unable
to read the Eclipse-compiled classes. If you hit that, build with:

```bash
./gradlew build -PecjWorkaround
```

This switches both the Minecraft recompile and the mod compilation to the Eclipse compiler
(ECJ, resolved from Maven Central at build time). Normal environments do not need it.

## Development

```bash
./gradlew runClient   # client
./gradlew runServer   # dedicated server
./gradlew runServer -Psmoketest=true
```

The smoke test logs the state of the mod (slot count, item registration, slot type checks,
equipping, command execution) once the server has started.

## Using the API

```java
IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
ItemStack ring = baubles.getStackInSlot(1);       // slot 1/2 are ring slots
int slot = BaublesApi.isBaubleEquipped(player, item);
```

An item becomes a bauble by implementing `baubles.api.IBauble`:

```java
public class MyCharm extends Item implements IBauble {
    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.CHARM;
    }

    @Override
    public void onWornTick(ItemStack stack, LivingEntity wearer) {
        // called every tick while worn
    }
}
```

Differences from the 1.12.2 API:

| 1.12.2 | 1.21.1 |
|---|---|
| `EntityLivingBase`/`EntityPlayer` | `LivingEntity`/`Player` |
| Item bauble capability | `stack.getItem() instanceof IBauble` (`BaublesApi.getBauble`) |
| `IRenderBauble#onPlayerBaubleRender(..., float)` | receives a `PoseStack` as well |
| `BaublesApi#getBaubles(EntityPlayer)` (IInventory) | removed |

## License

This project is a derivative of Baubles by Azanor and is distributed under
**CC BY-NC-SA 3.0** (Attribution-NonCommercial-ShareAlike 3.0 Unported).
See [LICENSE](LICENSE) and [NOTICE.md](NOTICE.md).

---

## 中文说明

这是 [Baubles（饰品栏）](https://github.com/Azanor/Baubles) 的社区移植版：把原作者 Azanor
停止维护的 1.12.2 版本搬到 **Minecraft 1.21.1 / NeoForge**。

保留 `baubles.api.*` 这套 API 是为了让当年针对 Baubles 1.5.2 写的模组（尤其是神秘时代 6）
能少改代码就搬过来。

**这不是官方续作**，与原作者无关，详见 [NOTICE.md](NOTICE.md) 与 [LICENSE](LICENSE)。

### 已有功能

- 七个饰品槽（护身符、腰带、头、身、护符、两个戒指），存放在玩家身上
- 扩展背包界面：原版 2x2 合成、护甲、副手槽都在
- 按 `B` 或在背包界面点按钮，在普通背包与饰品栏之间切换
- 实现 `IBauble` 的物品即可佩戴，`onWornTick` / `onEquipped` / `canEquip` 等回调照旧
- 随玩家存档保存、死亡与跨维度携带、死亡掉落（受 `keepInventory` 影响）
- 装备状态同步给附近客户端，实现 `IRenderBauble` 的物品会渲染在人物身上
- `/baubles view|clear <玩家> [槽位]`（别名 `/baub`、`/bau`）

### 构建

```bash
./gradlew build
```

产物在 `build/libs/`。如果遇到 `package-info.java: null` 的编译失败（部分机器的 javac
无法读取 NeoFormRuntime 的产物），改用：

```bash
./gradlew build -PecjWorkaround
```

### 许可证

本仓库是派生作品，沿用原作的 **CC BY-NC-SA 3.0**（署名—非商业—相同方式共享）。
转载、修改、再发布请保留 Azanor 的署名，且不得用于商业用途。
