Android EagleEye
========

Android EagleEye是一个基于[Xposed](http://repo.xposed.info/)和[adbi](https://github.com/crmulliner/adbi)的应用，可以实现对Android Java和Native方法的Hook，最终会将Hook的方法相关信息以Log的形式输出，包括应用的uid、方法名称、参数信息、返回值等。

**在使用Android EagleEye过程中对设备造成的任何风险自负**

<img src="screenshots/overview.png" width="490" height="135"/>

特色
--------
* 可实现对Android Java和Native方法的Hook
* 可根据配置文件定制需要Hook的Java方法
* 可Hook通过DexClassLoader加载的Java方法
* 可Hook系统库或应用程序自身的Native方法
* 针对模拟器逃逸技术采取了相应的对抗方法

文件内容
--------
* EagleEye/: Android EagleEye的源代码
* EagleEyeTest/: Android EagleEye的测试应用源码
* testcase_configs/: 自定制需Hook的Java API、方法以及Native库的配置文件，针对EagleEyeTest测试用例


使用方法
--------

**首先请确保设备已root并安装 [XposedInstaller](http://repo.xposed.info/module/de.robv.android.xposed.installer)**

按照以下步骤操作:

1. 安装*EagleEye.apk*并在XposedInstaller中的模块部分勾选Android EagleEye
2. 重启Android设备
4. 若需要Hook Java方法，则需要将*rw.eagleeye.fr.uids*属性设置为需Hook的目标应用程序的uid，多个应用程序的uid值以“|”分割；若需要Hook Native方法，则需要将*rw.eagleeye.nt.uids*属性设置为需Hook的目标应用程序的uid，例如:
	* *adb shell su -c setprop rw.eagleeye.fr.uids "10076|10078"* （Java Hook）
	* *adb shell su -c setprop rw.eagleeye.nt.uids "10076|10078"* （Native Hook）
5. **重启**需Hook的目标应用程序(Android EagleEye会在应用程序加载时读取*rw.eagleeye.fr.uids*和*rw.eagleeye.nt.uids*属性值，因此若希望更改需Hook的目标应用程序，需要重新设置改属性值，并重启对应的应用程序)
6. 使用命令 *adb logcat -s EagleEye:I* 查看Log信息

###如何HOOK更多的Java方法

**Android EagleEye已经预先定义了一些需Hook的Java系统API(*com.mindmac.eagleeye.hookclass*包中包含相关信息), 如果希望Hook其他系统API或者应用程序自身定义的方法，可以按照如下配置实现:**

1. 自定制系统API可以通过**fr_sys_apis.config**配置文件实现(配置文件格式请参考*Configure File Format* 部分)。编写完成后将该配置文件上传到设备的**/data/local/tmp/**目录下。 Android EagleEye默认会从配置文件中读取500条记录，可以通过设置**rw.eagleeye.fr.sys_api_num**属性修改。
2. 自定制应用程序的方法可以通过**fr_app_apis.config**配置文件实现(配置文件格式请参考*Configure File Format* 部分)。编写完成后将该配置文件上传到应用程序的data目录下，即**/data/data/\<package name of the application you want to hook\>/**。 Android EagleEye默认会从该配置文件中的读取500条记录，可以通过设置**rw.eagleeye.fr.app_api_num**属性修改。
3. **重启**需Hook的目标应用程序(Android EagleEye会在应用程序加载时读取上述配置文件)。

**请确保上述配置文件可以被需Hook的目标应用程序正常访问读取，必要时使用*chmod*命令**

###如何HOOK更多的Native方法
**EagleEye基于adbi实现对Native方法的Hook（无需注入部分），EagleEye已经默认Hook了部分系统Native方法，请参考jni目录下得*hook_apis.c*文件，若需要Hook其他的Native方法，需要通过添加额外的代码实现。请按照以下步骤实现对其他方法的Hook，唯一需要修改的源码文件为jni目录下的*hook_apis.c*。**

1. 声明目标方法的替代方法（参数类型、返回值类型需要和目标方法保持一致）。
2. 初始化**HOOK_INFO**结构体，若是Hook系统库中的方法，该变量应该以**system_hook_info**开头，若是Hook应用程序自身Native库中的方法，该变量应该以**custom_hook_info**开头。该结构体的第2个成员为目标库的名称（开头的lib是必需的）；第3个成员为目标方法名称；第4和最后一个成员为第1步中声明的方法名。
3. 实现步骤1中声明的方法，具体实现细节可以参考**hook_apis.c**。
4. 控制台下切换到Android EagleEye目录，执行**ndk-build**命令，重新编译Native库。
5. 重新安装修改后的Android EagleEye应用。
6. 若需要Hook应用程序自身Native库中的方法，还需要一个额外的配置文件**native_lib.config**。该配置文件用以设置目标库的名称，一个库名称一行，且库名称以**lib**开头，**.so**不需要。将该配置文件推送到目标应用程序的**data**目录下，即**/data/data/\<package name of the application you want to hook\>/**。请确保该文件可被目标应用程序读取。

Anti Anti-Emulator
--------
一些Android应用程序会在运行时检测是否运行在Android模拟器中。Android EagleEye基于Runtime Hook技术采取了一些对抗模拟器逃逸的方法，以Tim Strazzere的[anti-emulator](https://github.com/strazzere/anti-emulator)项目为例，以下输出是部署Android EagleEye后的输出：

V/AntiEmulator( 2226): Checking for Taint tracking...  
V/AntiEmulator( 2226): hasAppAnalysisPackage : false  
V/AntiEmulator( 2226): hasTaintClass : false  
V/AntiEmulator( 2226): hasTaintMemberVariables : false  
V/AntiEmulator( 2226): Taint tracking was not detected.  
V/AntiEmulator( 2226): Checking for Monkey user...  
V/AntiEmulator( 2226): isUserAMonkey : false  
V/AntiEmulator( 2226): Monkey user was not detected.  
V/AntiEmulator( 2226): Checking for debuggers...  
V/AntiEmulator( 2226): No debugger was detected.  
V/AntiEmulator( 2226): Checking for QEmu env...  
V/AntiEmulator( 2226): hasKnownDeviceId : false  
V/AntiEmulator( 2226): hasKnownPhoneNumber : false  
V/AntiEmulator( 2226): isOperatorNameAndroid : false  
V/AntiEmulator( 2226): hasKnownImsi : false  
V/AntiEmulator( 2226): hasEmulatorBuild:false  
V/AntiEmulator( 2226): hasPipes : false  
*V/AntiEmulator( 2226): hasQEmuDriver : true*  
V/AntiEmulator( 2226): hasQEmuFiles : false   
*V/AntiEmulator( 2226): hasEmulatorAdb :true*   
*V/AntiEmulator( 2226): hitsQemuBreakpoint : true*  
V/AntiEmulator( 2226): QEmu environment detected.

Android EagleEye目前仅考虑了对抗部分模拟器逃逸方法，当然这些也是使用较多的方法。


配置文件格式
--------
**fr_sys_apis.config**和**fr_app_apis.config**具有同样的格式，描述如下：

1. 需要Hook的系统API或应用自身方法以行分割；
2. Android EagleEye通过读取**rw.eagleeye.fr.sys_api_num** 和**rw.eagleeye.fr.app_api_num**属性值来限制可以Hook的API或方法个数，默认值为500。
2. 每个需要Hook的API或方法以*smali*的格式定义。例如，如果想要Hook[Intent.putExtra()](http://developer.android.com/reference/android/content/Intent.html#putExtra(java.lang.String, java.lang.String))方法，则可以如下定义:
	* Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
3. 将参数和返回值类型留空，可以实现对所有具有相同名称的API或方法进行Hook。例如, 通过如下配置可以Hook所有的[Intent](http://developer.android.com/reference/android/content/Intent.html)类下得*putExtra* API：
	* Landroid/content/Intent;->putExtra
4. 若需要hook构造函数，则配置格式为*Lpackage/name/class_name;->class_name(parameter_types)*. 例如，若要hook Intent的构造函数 Intent(String action), 可如下配置:
*Landroid/content/Intent;->Intent(Ljava/lang/String;)* 或者 *Landroid/content/Intent;->Intent*（hook Intent所有的构造函数）。
	
**native_lig.config**的格式相对简单。每个库名称一行，且库名称开头需要保留**lib**关键字，文件后缀**.so**则不需要。

可参看[testcase_configs](testcase_configs)下的配置文件获取更多信息。

Log格式
--------
Android EagleEye会将Hook方法的参数、返回值等信息以JSON的格式进行Log。目标应用程序的*uid*, *hook类型*（Java系统API-0x00，Java应用方法-0x01，Native系统API-x010，Native应用方法-0x11）也会被记录。对于预先定义的Java系统API，会记录参数的名称信息，而不是参数类型信息。

Log的内容大致如下所示，10078为目标应用的uid；16为Hook类型，此处表示Hook的是Native系统API，即open函数；false指示是否为自定制的Hook方法。

*{"Basic":["10078","16","false"],"InvokeApi":{"open":{"filename":"/data/data/com.mindmac.eagleeye/lib/libeagleeyenative.so","access":"0","permission":"8"},"return":{"int":"50"}}}*

Android的Logcat在Log内容超过1024字节时会将其截断，因此Android EagleEye会将文件读写的内容划分为多个部分，可以通过*id*值来重新组合。另外，文件路径、文件内容以十六进制格式记录，避免编码造成的乱码。文件读写相关的记录如下：

*{"Basic":["10078","0","false"], "FdAccess": {"path": "2f73746f726167652f656d756c617465642f302f44796e616d69632e61706b", "id": "1577699679" }}*

*{"Basic"["10078","0","false"],"FileRW":{ "operation": "write", "data": "504b0304140008080800f4b0d6463100c38daf020000fc06000013000400416e64726f69644d616e69666573742e786d6cfeca00008d543d6f1341149cbdb3938bf381f369c736120515c2074401457449400a52a0004145636207ac248e655f42a021252525053f00217e00250505bf809a9a3a652498f76e8f5b5f1cc19dc6bb3b3b3befeddb3dfb08703a0218d4f0dd072e227dde39fd0ab14c3c240eeddc07e213f185f846fc2042032c11778823e233f195f8459c10a744c5a38ed8209e12af89f7c447e22771428c334a0b3df4d1c63e3a58e76f93ccd9990768604f6726d9b6c93ca272074f0654401111953d3c271f9da319a76297ef3e5e628dfd2d6a0ed0e54c8e9a2dabca53d1c033aeddd5518417ecc739e4a848f21965afc3383dae6bb305ae5019f1ede236aef1edd3335edb603fcce8438db8475d832b76d8f6a8edeb78d0171aad6b336ee81e813219c934c236953d3aad71376d72cdccced3eafeff9ab4ee81e6df21bfadf945e4666dee219923558a6f8be3265ed91a4945011f37c85e67cfc312fb728e37950939aa13b7c82febbba2d10e1843e2d40939c5f8dcba1a413c25fbe44403ad898c0f8988b1c53fc47df272575687cc0a1f3182fcd6b927d97da47b0746fefac5fe953367e6ae0e07d4127515f75839c92bceb3c5b3923a4be44bfff0caae08b149bfc7f45bc706eeeab7796c025c655bf58ca91113449738263c1863087eedc697d61479de5277e0379f31b60519937febf0f25c607f8e6fdedeb6826d652ee0c7652c27edb4ee0ffeb49d9fd0af2fe68a969b643b1373c579cb4de9bd8975b3368f4dbdd9691e73360fcfc9c373f298b29cef70e2efdbbd8e65bc2446a43728e5e76d8c9c136334f563e146de14f42ea41cff0794cb0f709e72b9949b9136d957d189b930645fc3fcd947c9d6a864f3bf6ceb967895ad57f2886645b954b36835c68917d8ff79f1ae3835cbae4bfc161cbe7a8e5fcdfad51cbfecba842f65f690f0e54c9d127e317366095fcddc5b93e1937bfe07504b07083100c38daf020000fc060000504b03040a0000080000568a", "id": "1577699679"}}*

*{"Basic"["10078","0","false"],"FileRW":{ "operation": "write", "data": "d54662df48f24c1700004c170000240002007265732f6472617761626c652d686470692d76342f69635f6c61756e636865722e706e67000089504e470d0a1a0a0000000d494844520000004800000048080600000055edb347000017134944415478daed5b69905dc575fe4ef75dde326f66342369b401328b8d05212c31066313f00201dbb81c22657116d9a932492a4e2524ae2cae0a0f278e718c8dcb29db31210976e2251a6f0a0107a41805634258a25844022421242446d268f6b7dd7bbbfb9cfce8fb16c9d81ac733b64971abeebc59eebc77fbeb73beef3ba7fb022f1d2f1d2f1d2f1dff4f0f916ab027fdfd759bf75d3722025ad8f77e77f87476c34f6e3df4f6e1c51c835a3c7040ffb2ebf155bbf7effbebac9ebdefe66d6be3938124029a0f902255b5d3b80b0e8d4f7de1c96f8fff6a55aa6aa12760d1010280c7b78fcfec7ef2c8ae2cb5bf7266e5f48db73f7e51d01da4507bb0229bb4882860bd1ac57a2522caffde0f5c443ac089807635c6971f9bacddb2f3bfc7069efeafdab7aba80a116431c6102c1638441091ffac7df0ce4b3f562fa76fa82c897f2f6c15ef13a9eeef62f47b4583152f076a17103ebd92f1fafeb721a40c7fd710ac19cfc04f1cc32d3b4f07e6006093acd7bb005dcb66371e3e347dd1d8fee6a732173ebc38b1938f63b179a85aadaaf8ac7baf1f5e19de1ec56aeb9983a7fefe25eb2e3dcf22bd96e1de2230ab1d2c010286411313c8d002a001c4000a53cee9fb8ce37b9ab6fc8da4b9efac23e3935fddf1c8f88e47b7a46f7fd5cbaf9fad56abfca2050802baedce2b069a61fd96534eeffbf9cbcfb978d7507fe5150a6a10d0ca2121861382c0c2a1854934310d0786858513013b2d9c857576e5c7a6a666fb9f7874ec94677737de3962aebf7731c159d414ebe253a5f18d76d836c2208bf695fa43ba14509221510a408c214458420a1118161a87902003c3003010b6b0d6927169c59a992bc302c9d295f1b70787fa67c7be59850868b1f807791c2fa6ccab43983dbd1f239fa0a8f9b6580f840a054a3043050ca00fa722c410081104", "id": "1577699679"}}*

*{"Basic":["10078","16","false"],"InvokeApi":{"read":{"handle":"49","buffer":"26818ff2f1a451310b80e7207921660aa10f2ac4a051c5973b901a9bea951a9993760d015f4ff78ce3e0f85c6f281e882c799a9f471535b7de6b343affd06a5fab22b76f9403ac5339bf27dedb6d54ea866b9c82db31f6228b02bc588206cb24e2f77b2cc2871868128ba3e022fdb977abd5f098b93326ee3d5815d0377caea0aa64b4ac480827b01a0821720068e628ca6f56b40ede97d30a3173d2643e264832c748ac63e47ea8a9389e63ebfd6aa0f85ccb8166714849e01c015e5a0572fcf6c74379e8385a83ee30fd13a75c998064cfde1a494787538c5cddb6b2f8789bc45a15f74da855d9833f2a61def8d1aba7f8ddafe42e501b27a4c3416c28e83ffa3d4d9a1a85f7a1a695cdd383db12a6db96c51f982bb1e0d8dfd1625f285af884417661337be84c99839687","nbyte":"65557","id":"1444149052","path":"2f73746f726167652f656d756c617465642f302f44796e616d69632e61706b"},"return":{"int":"65557"}}}*

*{"Basic":["10078","16","false"],"InvokeApi":{"read":{"handle":"49","buffer":"5227cc95b7489f62915bbad3db9606d501d2a7cc1e7ac3a4b316ad58d98fb81613ec6a7cc2374c98b491293584cfe06749975e65ba1a8f3409c7f623359edd76544bb9a107b9de280303e2b068d3c012ae06dde01510e5bbb8a08d006bc458e26a54b01a60e7159de38f4dc79973c9303b23db28a07e615935721c0941b34ec43c58235975fcbd96fb8a1263e5fe8a9268e545d074ee49ef7dcac6af03f826f9369fec267a4e1e5c1c45599c483ea6d0966635229d649c2ab5c69e666ee1976322234682e0557f8144b1166b8aab7aa72226c9b917216529c95a94e63d17df8988e054c198aadc72a394e2427428d9a973b2190a0a9c581cf578afee20fa0cbdeb301917b2bf501b3a9d03d79924438ef22447457d27820dac18310a3750c9bdcd1e0a9a74e9cf82a038dfe5","nbyte":"65557","id":"1444149052","path":"2f73746f726167652f656d756c617465642f302f44796e616d69632e61706b"},"return":{"int":"65557"}}}*
