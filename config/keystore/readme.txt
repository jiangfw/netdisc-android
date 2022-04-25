keytool -list -v -keystore fuwei.keystore -storepass freshman870218

密钥库类型: JKS
密钥库提供方: SUN

您的密钥库包含 1 个条目

别名: fuwei
创建日期: 2022-4-24
条目类型: PrivateKeyEntry
证书链长度: 1
证书[1]:
所有者: CN=JiangFuwei, OU=Personal, O=Geeks, L=BeiJing, ST=BeiJing, C=China
发布者: CN=JiangFuwei, OU=Personal, O=Geeks, L=BeiJing, ST=BeiJing, C=China
序列号: 57068a47
生效时间: Sun Apr 24 15:13:59 CST 2022, 失效时间: Tue Mar 31 15:13:59 CST 2122
证书指纹:
	 SHA1: 1A:EA:6E:E0:3A:FC:8C:D4:D0:51:3A:34:11:98:C1:8C:91:B4:07:15
	 SHA256: A6:DC:8B:15:DA:BD:DA:FE:D2:86:ED:27:9A:32:1D:3D:18:5B:A5:A0:30:76:F0:4E:F1:75:8C:2A:04:7F:69:9D
签名算法名称: SHA256withRSA
主体公共密钥算法: 2048 位 RSA 密钥
版本: 3

扩展: 

#1: ObjectId: 2.5.29.14 Criticality=false
SubjectKeyIdentifier [
KeyIdentifier [
0000: E5 1A 2A D4 75 3F 3D 2C   12 F6 06 4D 48 68 66 E7  ..*.u?=,...MHhf.
0010: 07 D0 A4 26                                        ...&
]
]



*******************************************
*******************************************



Warning:
JKS 密钥库使用专用格式。建议使用 "keytool -importkeystore -srckeystore fuwei.keystore -destkeystore fuwei.keystore -deststoretype pkcs12" 迁移到行业标准格式 PKCS12。
