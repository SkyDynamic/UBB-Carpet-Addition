\>\>\> [返回索引](/README.md)

# 规则

## 珍珠珍珠自加载 (pearlTickets)

开启后, 若末影珍珠的途径区块未加载, 则会使路径上**需要加载的区块**暂时变为**加载等级为30**的加载票

> 实在不想做任何加载链可以用这个，但是小心被放飞(因为末影珍珠会一直加载到碰撞并传送为止)

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false` , `true`
- 分类: `SCA` , `FEATURE` , `SCA_chunkLoader`


## 音符盒区块加载 (noteBlockChunkLoader)

开启后，当上边沿红石信号激活音符盒时，为该音符盒所在区块添加类型为"note_block"，加载等级为30的加载票，持续时间为300gt（15s）。
<br>
`bone_block`: 音符盒上有骨块时可以触发加载。
<br>
`wither_skeleton_skull`: 音符盒上有凋灵骷髅头（可以是挂在墙上的也可以是放在音符盒上的）时可以触发加载。
<br>
`note_block`: 无需条件，只有音符盒即可加载。
<br>
`OFF`: 禁用该规则。

- 类型: `String`
- 默认值: `OFF`
- 参考选项: `bone_block` , `wither_skeleton_skull` , `note_block` , `OFF`
- 分类: `SCA` , `FEATURE` , `SCA_chunkLoader`

## 活塞头区块加载 (pistonBlockChunkLoader)

开启后，当该活塞/黏性活塞产生活塞头的推出/拉回事件时，在创建推出/拉回事件的那一游戏刻为**活塞头方块所在区块**添加类型为"piston_block"，加载等级为30的加载票，持续时间为300gt（15s）。注意，黏性活塞的失败收回事件（如尝试拉回超过12个方块时）也可创建加载票。
<br>
`bone_block`: 活塞\黏性活塞上有骨块时触发加载。
<br>
`bedrock`: 活塞\黏性活塞下有基岩时触发加载。
<br>
`all`: 活塞\黏性活塞下有骨块或基岩时触发加载。
<br>
`OFF`: 禁用该规则。
> 如果不想使用地狱门加载链的话，此规则可作为替代方案。

- 类型: `String`
- 默认值: `OFF`
- 参考选项: `bone_block` , `bedrock` , `all` , `OFF`
- 分类: `SCA` , `FEATURE` , `SCA_chunkLoader`


## 易碎深板岩 (softDeepslate)
> 需客户端支持

开启后，深板岩的挖掘硬度将与石头相同（均可在急迫二效果下用效率5钻石镐进行瞬间挖掘）。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false` , `true`
- 分类: `SCA` , `FEATURE` , `SURVIVAL`


## 计划刻催熟仙人掌 (scheduledRandomTickAllPlants)

开启后，使计划刻事件可触发仙人掌的随机刻生长行为。

<该规则从 [OhMyVanillaMinecraf](https://github.com/hit-mc/OhMyVanillaMinecraft) 移植>

- 类型: `boolean`

- 默认值: `false`

- 参考选项: `false` , `true`

- 分类: `SCA` , `FEATURE` , `SURVIVAL`


## 可合成农夫山泉 (craftingNongfuSrping)

开启后，便可以合成农夫山泉

```
合成方式

空 下界之星 空
空  玻璃瓶  空
铁锭  空  铁锭
```

- 类型: `boolean`

- 默认值: `false`

- 默认选项: `false` , `true`

- 分类: `SCA` , `CRAFTING`, `SURVIVAL`

## tpa命令 (commandTpa)

开启后可以使用`/tpa <玩家名> `向某位玩家请求传送 ，传送请求会在一段时间内(`commandTpaTimeout`)过期  
收到请求的玩家可以使用`tpaccept`接受传送请求，或者使用`tpdeny`拒绝传送请求  

- 类型: `boolean`

- 默认值: `false`

- 默认选项: `false` , `true`

- 分类: `SCA` , `CRAFTING`, `SURVIVAL`


## tpa请求超时 (commandTpaTimeout)

设置tpa请求超时时长 单位为秒

- 类型: `int`

- 默认值: `300`

- 分类: `SCA` , `CRAFTING`, `SURVIVAL`

