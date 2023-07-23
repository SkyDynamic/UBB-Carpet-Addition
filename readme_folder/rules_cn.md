\>\>\> [返回索引](/README.md)

# 规则

## 珍珠珍珠自加载 (pearlTickets)

开启后, 若末影珍珠的途径区块未加载, 则会使路径上**需要加载的区块**暂时变为**加载等级为30**的加载票

> 实在不想做任何加载链可以用这个，但是小心被放飞(因为末影珍珠会一直加载到碰撞并传送为止)

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false` , `true`
- 分类: `SCA` , `FEATURE` , `SCA_chunkLoader`

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

- 分类: `SCA` , `TPA`, `FEATURE`


## tpa请求超时 (commandTpaTimeout)

设置tpa请求超时时长 单位为秒

- 类型: `int`

- 默认值: `300`

- 分类: `SCA` , `TPA`, `FEATURE`

## tpa传送前等待时间 (commandTpaTeleportWaits)

设置tpa传送发生之前的等待时间，单位为秒, 当值为0时禁用传送等待

- 类型: `int`

- 默认值: `5`

- 分类: `SCA` , `TPA`, `FEATURE`

