package com.mindmac.eagleeye.hookclass;

import java.util.ArrayList;
import java.util.List;

import com.mindmac.eagleeye.MethodParser;
import com.mindmac.eagleeye.Util;
import com.mindmac.eagleeye.service.Launcher;

import android.content.Intent;
import android.os.Binder;
import android.text.TextUtils;
import android.util.Log;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class ContextImplHook extends MethodHook {
	private Methods mMethod;
	private static final String mClassName = "android.app.ContextImpl";
	
	private ContextImplHook(Methods method) {
		super(mClassName, method.name());
		mMethod = method;
	}


	// public PackageManager getPackageManager()
	// public Object getSystemService(String name)
	// public ComponentName startService(Intent service)
	// public void startActivities (Intent[] intents, Bundle options)
	// public void startActivities (Intent[] intents)
	// public void startActivity (Intent intent)
	// public void startActivity (Intent intent, Bundle options)
	// frameworks/base/core/java/android/app/ContextImpl.java
	// public void sendBroadcast (Intent intent, String receiverPermission)
	// public void sendBroadcast (Intent intent)
	// public void sendOrderedBroadcast (Intent intent, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras)
	// public void sendOrderedBroadcast (Intent intent, String receiverPermission)

	private enum Methods {
		getPackageManager, getSystemService, startService, startActivity, sendBroadcast, sendOrderedBroadcast
	};

	public static List<MethodHook> getMethodHookList() {
		List<MethodHook> methodHookList = new ArrayList<MethodHook>();
		methodHookList.add(new ContextImplHook(Methods.getPackageManager));
		methodHookList.add(new ContextImplHook(Methods.getSystemService));
		methodHookList.add(new ContextImplHook(Methods.startService));
		methodHookList.add(new ContextImplHook(Methods.startActivity));
		methodHookList.add(new ContextImplHook(Methods.sendBroadcast));
		methodHookList.add(new ContextImplHook(Methods.sendOrderedBroadcast));
		return methodHookList;
	}

	protected void logBroadcast(int uid, MethodHookParam param, String argNames){
		// Check if need log
		if(!this.isNeedLog(uid))
			return;

		String[] argNamesArray = null;
		if(argNames != null)
			argNamesArray = argNames.split("\\|");
		String formattedArgs = MethodParser.parseMethodArgs(param, argNamesArray);
		if(formattedArgs == null)
			formattedArgs = "";
		Intent intent = (Intent) param.args[0];
		if (!intent.getExtras().isEmpty()) {
			formattedArgs += " extra="+intent.getExtras().toString();
			TextUtils.concat("asd");
		}
		String returnValue = MethodParser.parseReturnValue(param);
		String logMsg = String.format("{\"Basic\":[\"%d\",\"%s\",\"false\"], " +
						"\"InvokeApi\":{\"%s->%s\":{%s}, \"return\":{%s}}}", uid, Util.FRAMEWORK_HOOK_SYSTEM_API,
				this.getClassName(), this.getMethodName(), formattedArgs, returnValue);
/*		if (param.args[0].getClass().equals(Intent.class)) {
			Intent intent = (Intent) param.args[0];
			Log.i("ZCTYM", intent.getExtras().toString());
			Log.i("ZCTYM",intent.toString());
			Log.i("ZCTYM", intent.getExtras().keySet().toString());
		}*/
		Log.i(Util.LOG_TAG, logMsg);
	}

	@Override
	public void after(MethodHookParam param) throws Throwable {
		int uid = Binder.getCallingUid();
		String argNames = null;
		
		if (mMethod == Methods.getPackageManager) {
			Object instance = param.getResult();
			if (instance != null)
				Launcher.hookSystemServices(this, "PackageManager", instance);
			return;
		} else if (mMethod == Methods.getSystemService) {
			if (param.args.length > 0 && param.args[0] != null) {
				String name = (String) param.args[0];
				Object instance = param.getResult();
				if (name != null && instance != null)
					Launcher.hookSystemServices(this, name, instance);
			}
			return;
		}else if(mMethod == Methods.startService){
			argNames = "service";
		}else if(mMethod == Methods.startActivity){
			if(param.args.length == 2)
				argNames = "intent|options";
			else
				return;
		}
		else if (mMethod == Methods.sendBroadcast)
		{
			if(param.args.length == 2)
				argNames = "intent|receiverPermission";
			else
				argNames = "intent";
			logBroadcast(uid, param, argNames);
		}
		else if (mMethod == Methods.sendOrderedBroadcast)
		{
			if(param.args.length == 7)
				argNames = "intent|receiverPermission|resultReceiver|scheduler|initialCode|initialData|initialExtras";
			else
				argNames = "intent|receiverPermission";
			logBroadcast(uid, param, argNames);
		}
		log(uid, param, argNames);
	}
}
