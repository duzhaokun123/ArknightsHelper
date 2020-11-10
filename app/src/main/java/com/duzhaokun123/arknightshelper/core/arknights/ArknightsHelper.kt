package com.duzhaokun123.arknightshelper.core.arknights

import com.duzhaokun123.arknightshelper.Application
import com.duzhaokun123.arknightshelper.core.Flags
import com.duzhaokun123.arknightshelper.core.logger.MDLogger
import com.duzhaokun123.arknightshelper.core.UIInteractor
import com.duzhaokun123.arknightshelper.core.imgreco.ocr.*
import com.duzhaokun123.arknightshelper.core.imgreco.ocr.Util.crop
import com.duzhaokun123.arknightshelper.core.imgreco.ocr.Util.sum
import com.duzhaokun123.arknightshelper.core.logger.AndroidLogger
import com.duzhaokun123.arknightshelper.core.logger.CallbackLogger
import com.duzhaokun123.arknightshelper.core.logger.MultiLogger
import com.duzhaokun123.arknightshelper.core.model.*
import com.duzhaokun123.arknightshelper.utils.TipUtil
import com.duzhaokun123.overlaywindow.OverlayService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Rect
import kotlin.random.Random

class ArknightsHelper(
    private val uiInteractor: UIInteractor,
    callbackLogger: CallbackLogger,
    val config: ArknightsHelperConfig,
    private val controlWindowAction: OverlayService.WindowAction? = null,
    logName: String = "ArknightsHelper"
) {
    companion object {
        const val TAG = "ArknightsHelper"
    }

    private val logger by lazy {
        MultiLogger(MDLogger(logName), callbackLogger, AndroidLogger())
    }

    private val screenSize by lazy {
        uiInteractor.screenSize.also {
            logger.debug(TAG, "screenSize", "screenSize: $it")
        }
    }

    private val helperState by lazy { ArknightsHelperState() }

    fun quickStart() {

    }

    /**
     * @param times 0 无限
     */
    fun operationLoop(times: Int = 0) {
        val func = "operationLoop"
        logger.logH1(TAG, func)
        helperState.operationTime.removeAll { true }
        var count = 0
        while (times == 0 || count != times) {
            logger.info(TAG, func, "开始第 ${count + 1} 次")
            if (operationOnceStatemachine()) {
                count++
            } else {
                logger.error(TAG, func, "在第 ${count + 1} 次失败")
                break
            }
            logger.logDivider(TAG, func)
        }
        logger.logDivider(TAG, func)
        logger.info(TAG, func, "$count 次战斗完成")
    }

    fun login() {
        val (vw, vh) = Util.getVwvh(screenSize)

        interactorSpace {
            clickButton(
                Point(50 * vw - 14.074 * vh, 66.574 * vh),
                Point(50 * vw + 13.981 * vh, 75.463 * vh)
            )
        }
    }

    fun goHome(): Boolean {
        val func = "goHome"
        logger.info(TAG, func, "正在返回主页")

        val (vw, vh) = Util.getVwvh(screenSize)

        while (true) {
            var screenshot: Mat? = null
            interactorSpace {
                screenshot = uiInteractor.getScreencap()
            }

            if (Main.checkMain(screenshot!!, logger)) {
                break
            }

            if (Common.checkNavButton(screenshot!!, logger)) {
                logger.info(TAG, func, "发现返回按钮，点击返回")
                logger.logDivider(TAG, func)
                interactorSpace {
                    clickButton(
                        Point(3.194 * vh, 2.222 * vh),
                        Point(20.972 * vh, 7.917 * vh)
                    )
                }
                _wait(Flags.SMALL_WAIT)
                continue
            }

            if (Common.checkGetItemPopup(screenshot!!, logger)) {
                logger.info(TAG, func, "当前为获得物资画面，关闭")
                logger.logDivider(TAG, func)
                interactorSpace {
                    clickButton(
                        Point(100 * vw - 61.944 * vh, 18.519 * vh),
                        Point(100 * vw - 5.833 * vh, 87.222 * vh)
                    )
                }
                _wait(Flags.SMALL_WAIT)
                continue
            }

            if (Common.checkSettingScreen(screenshot!!, logger)) {
                logger.info(TAG, func, "当前为设置/邮件画面，返回")
                logger.logDivider(TAG, func)
                interactorSpace {
                    clickButton(
                        Point(4.722 * vh, 3.750 * vh),
                        Point(19.444 * vh, 8.333 * vh)
                    )
                }
                _wait(Flags.SMALL_WAIT)
                continue
            }

            // FIXME: 20-10-31 寻找关闭按钮
//            val rectConfidence = Common.findCloseButton(screenshot!!)
//            if (rectConfidence.second > 0.9) {
//                logger.info(TAG, "goHome: 发现关闭按钮")
//                interactorSpace { clickButton(rectConfidence.first) }
//                Thread.sleep(Flags.SMALL_WAIT)
//                continue
//            }
            // TODO: 20-10-31 checkDialog
            logger.error(TAG, func, "未知画面")
            return false
        }

        logger.info(TAG, func, "已回到主页")
        return true
    }

    fun setDelegate() {
        val (vw, vh) = Util.getVwvh(screenSize)

        interactorSpace {
            clickButton(
                Point(100 * vw - 32.500 * vh, 79.537 * vh),
                Point(100 * vw - 5.370 * vh, 85.000 * vh)
            )
        }
    }

    /**
     * @return true 安全退出
     */
    fun quitGame(packageName: String = "com.hypergryph.arknights"): Boolean {
        val func = "quitGame"
        logger.logH2(TAG, func)

        val goHomeRe = goHome()
        return if (goHomeRe) {
            uiInteractor.clickBack()
            Thread.sleep(Flags.TINY_WAIT)
            interactorSpace {
                clickButton(
                    Common.getDialogRightButtonRect(uiInteractor.getScreencap())
                )
            }
            logger.info(TAG, func, "已退出游戏")
            true
        } else {
            logger.error(TAG, func, "未能返回主页 强行结束游戏")
            uiInteractor.forceStop(packageName)
            false
        }
    }

    data class OperationOnceState(
        var state: State = State.NONE,
        var stop: Boolean = false,
        var operationStart: Long = 0,
        var firstWait: Boolean = true,
        var mistakenDelegation: Boolean = false,
        var prepareReco: Any? = null
    ) {
        enum class State {
            NONE,
            ON_PREPARE,
            ON_TROOP,
            ON_OPERATION,
            ON_LEVEL_UP_POPUP,
            ON_END_OPERATION
        }
    }

    fun operationOnceStatemachine(cid: String? = null): Boolean {
        val func = "operationOnceStatemachine"
        logger.logH2(TAG, func)
        val smobj = OperationOnceState()
        smobj.state = OperationOnceState.State.ON_PREPARE
        smobj.stop = false
        smobj.operationStart = 0

        while (smobj.stop.not()) {
            val oldState = smobj.state
            val result = when (smobj.state) {
                OperationOnceState.State.ON_PREPARE -> onPrepare(smobj, cid, config)
                OperationOnceState.State.ON_TROOP -> onTroop(smobj)
                OperationOnceState.State.ON_OPERATION -> onOperation(smobj)
                OperationOnceState.State.ON_LEVEL_UP_POPUP -> onLevelUpPopup(smobj)
                OperationOnceState.State.ON_END_OPERATION -> onEndOperation(smobj)
                OperationOnceState.State.NONE -> false
            }
            if (!result) {
                logger.error(TAG, func, "在 $oldState 时失败")
                return false
            }
            if (smobj.state != oldState) {
                logger.debug(TAG, func, "state change to ${smobj.state}")
            }
        }

        if (smobj.mistakenDelegation && config.behavior.skipMistakenDelegation.not()) {
            return false
        }
        return true
    }

    /**
     * FIXME: 理智恢复未测试
     *
     * @param cid 关卡号 null: 不限制
     * @return true: 可以开始 false: 无法开始
     */
    fun onPrepare(
        smobj: OperationOnceState,
        cid: String? = null,
        config: ArknightsHelperConfig
    ): Boolean {
        val func = "onPrepare"
        logger.logH2(TAG, func)
        val (vw, vh) = Util.getVwvh(screenSize)

        var recoResult: BeforeOperationRecognizeInfo?

        var count = 0
        while (true) {
            var screenshot: Mat? = null
            interactorSpace {
                screenshot = uiInteractor.getScreencap()
            }
            logger.getChild("BeforeOperation_recognize").use {
                recoResult = BeforeOperation.recognize(screenshot!!, it)
            }
            if (recoResult != null) {
                logger.debug(TAG, func, "当前画面关卡：${recoResult!!.operation}")
                if (cid != null) {
                    // 如果传入了关卡 ID，检查识别结果
                    if (cid != recoResult!!.operation) {
                        logger.error(TAG, func, "不在关卡界面 is ${recoResult!!.operation} not $cid")
                        return false
                    }
                }
                break
            } else {
                count++
                if (count <= 7) {
                    logger.warring(TAG, func, "不在关卡界面")
                    _wait(Flags.TINY_WAIT, false)
                } else {
                    logger.error(TAG, func, "${count}次检测后都不在关卡界面")
                    return false
                }
            }
        }

        val currentStrength = recoResult!!.ap.split('/')[0].toInt()
        val apText = if (recoResult!!.consumeAp) "理智" else "门票"
        val apConsume = recoResult!!.consume
        logger.logText(TAG, func, "当前$apText $currentStrength, 关卡消耗 $apConsume")
        if (currentStrength < apConsume) {
            logger.error(TAG, func, "${apText}不足 无法继续")
            if (recoResult!!.consumeAp && config.canRefill) {
                logger.logText(TAG, func, "尝试回复理智")
                interactorSpace {
                    clickButton(
                        Point(100 * vw - 30.972 * vh, 88.241 * vh),
                        Point(100 * vw - 3.611 * vh, 95.556 * vh)
                    )
                }
                _wait(Flags.SMALL_WAIT)
                var screenshot: Mat? = null
                interactorSpace {
                    screenshot = uiInteractor.getScreencap()
                }
                val refillType: Int
                logger.getChild("BeforeOperation_checkApRefillType").use {
                    refillType = BeforeOperation.checkApRefillType(screenshot!!, it)
                }
                var confirmRefill = false
                if (refillType == BeforeOperation.REFILL_TYPE_ITEM && config.refillWithItem) {
                    logger.logText(TAG, func, "使用道具回复理智")
                    confirmRefill = true
                }
                if (refillType == BeforeOperation.REFILL_TYPE_ORIGINIUM && config.refillWithOriginium) {
                    logger.logText(TAG, func, "碎石回复理智")
                    confirmRefill = true
                }
                if (confirmRefill) {
                    interactorSpace {
                        clickButton(
                            Point(50 * vw + 49.537 * vh, 77.222 * vh),
                            Point(50 * vw + 74.352 * vh, 84.815 * vh)
                        )
                    }
                    helperState.refillCount++
                    _wait(Flags.MEDIUM_WAIT)
                    return true
                }
                logger.error(TAG, func, "未能回复理智")
                interactorSpace {
                    clickButton(
                        Point(50 * vw + 14.259 * vh, 77.130 * vh),
                        Point(50 * vw + 24.352 * vh, 83.611 * vh)
                    )
                }
            }
            return false
        }

        if (recoResult!!.delegated.not()) {
            logger.logText(TAG, func, "设置代理指挥")
            setDelegate()
            return true
        }

        logger.logText(TAG, func, "${apText}充足 开始行动")
        interactorSpace {
            clickButton(
                Point(100 * vw - 30.972 * vh, 88.241 * vh),
                Point(100 * vw - 3.611 * vh, 95.556 * vh)
            )
        }
        smobj.prepareReco = recoResult
        smobj.state = OperationOnceState.State.ON_TROOP

        return true
    }

    /**
     * @return true: 成功开始 false: 失败
     */
    fun onTroop(smobj: OperationOnceState): Boolean {
        val func = "onTroop"
        logger.logH2(TAG, func)

        val (vw, vh) = Util.getVwvh(screenSize)

        val ready: Boolean

        var count = 0
        while (true) {
            _wait(Flags.TINY_WAIT, false)
            var screenshot: Mat? = null
            interactorSpace {
                screenshot = uiInteractor.getScreencap()
            }
            val recoResult: Boolean
            logger.getChild("BeforeOperation_checkConfirmTroopRect").use {
                recoResult = BeforeOperation.checkConfirmTroopRect(screenshot!!, it)
            }
            if (recoResult) {
                logger.info(TAG, func, "确认编队")
                ready = true
                break
            } else {
                count++
                if (count <= 7) {
                    logger.warring(TAG, func, "等待确认编队")
                } else {
                    logger.error(TAG, func, "${count}次检测后不再确认编队界面")
                    ready = false
                    break
                }
            }
        }

        if (ready) {
            interactorSpace {
                clickButton(
                    Point(50 * vw + 55.833 * vh, 52.963 * vh),
                    Point(50 * vw + 72.778 * vh, 87.361 * vh)
                )
            }
            smobj.operationStart = System.currentTimeMillis()
            smobj.state = OperationOnceState.State.ON_OPERATION
        }

        return ready
    }

    fun onOperation(smobj: OperationOnceState): Boolean {
        val func = "onOperation"
        logger.logH2(TAG, func)

        val (vw, vh) = Util.getVwvh(screenSize)

        val waitTime: Long
        if (smobj.firstWait) {
            waitTime =
                if (helperState.operationTime.size == 0) {
                    Flags.BATTLE_NONE_DETECT_TIME
                } else {
                    helperState.operationTime.sum() / helperState.operationTime.size - 7000
                }
            logger.info(TAG, func, "等待 ${waitTime / 1000.0} s")
            _wait(waitTime, false)
            smobj.firstWait = false
        }
        val t = System.currentTimeMillis() - smobj.operationStart

        logger.info(TAG, func, "已进行 ${t / 1000.0} s，判断是否结束")

        val screenshot = interactorSpace { uiInteractor.getScreencap() }

        if (EndOperation.checkLevelUpPopup(screenshot)) {
            logger.info(TAG, func, "等级提升")
            helperState.operationTime.add(t)
            smobj.state = OperationOnceState.State.ON_LEVEL_UP_POPUP
            return true
        }
        if (EndOperation.checkEndOperation(screenshot) || EndOperation.checkEndOperationAlt(
                screenshot
            )
        ) {
            logger.info(TAG, func, "战斗结束")
            helperState.operationTime.add(t)
            if (waitForStillImage(
                    crop = Rect(
                        Point(68.241 * vh, 61.111 * vh),
                        Point(100 * vw, 100 * vh)
                    ), timeOut = 15000
                ) != null
            ) {
                _wait(Flags.TINY_WAIT, false)
                smobj.state = OperationOnceState.State.ON_END_OPERATION
                return true
            }
        }

        val dialog = Common.recognizeDialog(screenshot)
        if (dialog?.ocrResult != null) {
            if (dialog.type == CommonCheckDialogInfo.Type.YES_NO && "代理指挥" in dialog.ocrResult) {
                logger.warring(TAG, func, "代理指挥出现失误")
                smobj.mistakenDelegation = true
                if (config.behavior.allowMistakenDelegation) {
                    logger.info(TAG, func, "以 2 星结算关卡")
                    interactorSpace {
                        clickButton(Common.getDialogRightButtonRect(screenshot))
                    }
                    _wait(2000)
                    return true
                } else {
                    logger.info(TAG, func, "放弃关卡")
                    interactorSpace {
                        clickButton(Common.getDialogLeftButtonRect(screenshot))
                    }
                    waitForStillImage()
                    interactorSpace {
                        clickButton(
                            100 * vw - 61.944 * vh,
                            18.519 * vh,
                            100 * vw - 5.833 * vh,
                            87.222 * vh
                        )
                    }
                    _wait(1000)
                    return true
                }
            } else if (dialog.type == CommonCheckDialogInfo.Type.YES_NO && "将会恢复" in dialog.ocrResult) {
                logger.info(TAG, func, "发现放弃行动提示，关闭")
                interactorSpace {
                    clickButton(Common.getDialogLeftButtonRect(screenshot))
                }
            } else {
                logger.error(TAG, func, "未处理的对话框：[${dialog.type}] ${dialog.ocrResult}")
                return false
            }
        }

        logger.info(TAG, func, "战斗未结束")
        _wait(Flags.BATTLE_FINISH_DETECT)
        return true
    }

    fun onLevelUpPopup(smobj: OperationOnceState): Boolean {
        val func = "onLevelUpPopup"
        logger.logH2(TAG, func)

        val (vw, vh) = Util.getVwvh(screenSize)

        _wait(Flags.SMALL_WAIT)
        logger.info(TAG, func, "关闭升级提示")
        interactorSpace {
            clickButton(
                Point(100 * vw - 67.315 * vh, 16.019 * vh),
                Point(100 * vw - 5.185 * vh, 71.343 * vh)
            )
        }
        waitForStillImage()
        smobj.state = OperationOnceState.State.ON_END_OPERATION
        return true
    }

    fun onEndOperation(smobj: OperationOnceState): Boolean {
        val func = "onEndOperation"
        logger.logH2(TAG, func)

        val (vw, vh) = Util.getVwvh(screenSize)

        val screenshot = interactorSpace { uiInteractor.getScreencap() }
        logger.info(TAG, func, "离开结算画面")
        interactorSpace {
            clickButton(50 * vw - 23.426 * vh, 23.981 * vh, 50 * vw + 31.389 * vh, 52.500 * vh)
        }
        // TODO: 上报企鹅数据
        // reportresult = penguin_stats.reporter.ReportResult.NotReported
        try {
            val drops: EndOperationRecognizeInfo?
            logger.getChild("EndOperation_recognize").use {
                drops = EndOperation.recognize(screenshot, it)
            }
            logger.info(TAG, func, "掉落识别结果 $drops")
            // TODO: 20-11-1 掉落识别
        } catch (e: Exception) {
            e.printStackTrace()
            logger.debug(TAG, func, "${e.message}")
        }
        smobj.stop = true
        return true
    }

    // FIXME: 20-10-31 正常工作, 但不知道为什么
    fun waitForStillImage(threshold: Int = 21, crop: Rect? = null, timeOut: Int = 60000): Mat? {
        val func = "waitForStillImage"
        var screenshot: Mat? = null
        interactorSpace {
            screenshot = uiInteractor.getScreencap()
        }
        if (crop != null) {
            screenshot = screenshot!!.crop(crop)
        }
        val t0 = System.currentTimeMillis()
        val ts = t0 + timeOut
        var n = 0
        var minerr = 65025.0
        while (System.currentTimeMillis() < ts) {
            _wait(1000, false)
            var screenshot2: Mat? = null
            interactorSpace {
                screenshot2 = uiInteractor.getScreencap()
            }
            if (crop != null) {
                screenshot2 = screenshot2!!.crop(crop)
            }
            val mse = Imgops.compareMse(screenshot!!, screenshot2!!)
            logger.debug(TAG, func, "mse = $mse, threshold = $threshold, minerr = $minerr")
            if (mse.sum() <= threshold) {
                return screenshot2
            }
            screenshot = screenshot2
            if (mse.sum() < minerr) {
                minerr = mse.sum()
            }
            n++
            if (n == 9) {
                logger.info(TAG, func, "等待画面静止")
                n = 0
            }
        }
        logger.error(TAG, func, "$timeOut 秒内画面未静止，最小误差 = $minerr，阈值 = $threshold")
        return null
    }

    fun clearDailyTask() {
        val func = "clearDailyTask"
        logger.logH1(TAG, func)
        logger.info(TAG, func, "领取每日任务")
        goHome()
        val screenshot = interactorSpace { uiInteractor.getScreencap() }
        logger.info(TAG, func, "进入任务界面")
        // TODO: 20-11-9
    }

    fun testBeforeOperationRecognize(img: Mat? = null) {
        val testImg = img ?: interactorSpace { uiInteractor.getScreencap() }

        val beforeOperationRecognizeInfo: BeforeOperationRecognizeInfo?
        logger.getChild("BeforeOperation_recognize_test").use {
            beforeOperationRecognizeInfo = BeforeOperation.recognize(testImg, it)
        }
        Application.runOnUiThread {
            TipUtil.showToast(beforeOperationRecognizeInfo.toString())
        }
    }

    fun testBeforeOperationCCTR(img: Mat? = null) {
        val testImg = img ?: interactorSpace { uiInteractor.getScreencap() }

        val result: Boolean
        logger.getChild("BeforeOperation_CCTR_test").use {
            result = BeforeOperation.checkConfirmTroopRect(testImg, it)
        }
        Application.runOnUiThread {
            TipUtil.showToast(result.toString())
        }
    }

    fun testEndOperationRecognize(img: Mat? = null) {
        val testImg = img ?: interactorSpace { uiInteractor.getScreencap() }

        val endOperationRecognizeInfo: EndOperationRecognizeInfo?
        logger.getChild("EndOperation_recognize_test").use {
            endOperationRecognizeInfo = EndOperation.recognize(testImg, it)
        }
        Application.runOnUiThread {
            TipUtil.showToast(endOperationRecognizeInfo.toString())
        }
    }

    fun testStartOperation() {
        logger.logH2(TAG, "testStartOperation")
        val smobj = OperationOnceState()
        val re = onPrepare(smobj, config = config)
        if (re) {
            onTroop(smobj)
        }
    }

    private fun clickButton(rect: Rect) = clickButton(
        rect.x.toDouble(),
        rect.y.toDouble(),
        rect.width.toDouble(),
        rect.height.toDouble()
    )

    private fun clickButton(
        left: Double,
        upper: Double,
        right: Double,
        lower: Double,
    ) = clickButton(Point(left, upper), Point(right, lower))

    private fun clickButton(a: Point, b: Point) {
        val func = "clickButton"
        logger.debug(TAG, func, "a: $a, b: $b")
        val x =
            if (a.x < b.x) {
                Random.nextDouble(a.x, b.x)
            } else {
                Random.nextDouble(b.x, a.x)
            }
        val y =
            if (a.y < b.y) {
                Random.nextDouble(a.y, b.y)
            } else {
                Random.nextDouble(b.y, a.y)
            }

        logger.debug(TAG, func, "x: $x, y: $y")
        uiInteractor.touch(x, y)
    }

    private fun _wait(tMs: Long = 10000, manlike: Boolean = true) {
        val n = if (manlike) {
            val m = Random.nextDouble(0.0, 0.3)
            Random.nextLong((tMs - m * 0.5 * tMs).toLong(), (tMs + m * tMs).toLong())
        } else tMs
        Thread.sleep(n)
    }

    private fun <R> interactorSpace(block: () -> R): R {
        controlWindowAction?.hide()
        val r = block()
        GlobalScope.launch {
            delay(500)
            controlWindowAction?.show()
        }
        return r
    }
}