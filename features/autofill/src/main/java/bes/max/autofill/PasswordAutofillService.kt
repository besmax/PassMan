package bes.max.autofill

import android.R
import android.app.assist.AssistStructure
import android.os.Build
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.Field
import android.service.autofill.FillCallback
import android.service.autofill.FillContext
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.Presentations
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import bes.max.cipher.api.CipherApi
import bes.max.database.api.repositories.SiteInfoDbRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PasswordAutofillService : AutofillService() {

    @Inject
    lateinit var cipher: CipherApi

    @Inject
    lateinit var siteInfoRepository: SiteInfoDbRepository

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        // Get the structure from the request
        val context: List<FillContext> = request.fillContexts
        val structure: AssistStructure = context[context.size - 1].structure

        val autofillMap = mutableMapOf<Field, AutofillId>()
        parseStructure(structure, autofillMap)

        if (autofillMap.containsKey(Field.PASSWORD)) {
            serviceScope.launch {
                val appPasswords = siteInfoRepository.getAll(Dispatchers.IO).firstOrNull()
                appPasswords?.let { passwords ->
                    val fillResponse: FillResponse = FillResponse.Builder()
                        .apply {
                            passwords.forEach { password ->
                                val passwordPresentation =
                                    RemoteViews(packageName, R.layout.simple_list_item_1).apply {
                                        setTextViewText(
                                            android.R.id.text1,
                                            "Password for ${password.name}"
                                        )
                                    }
                                val dataSetBuilder = Dataset.Builder()
                                val autofillValue = AutofillValue.forText(
                                    cipher.decrypt(
                                        password.name,
                                        password.password,
                                        password.passwordIv
                                    )
                                )
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    dataSetBuilder.setField(
                                        autofillMap[Field.PASSWORD]!!,
                                        android.service.autofill.Field.Builder()
                                            .setPresentations(
                                                Presentations.Builder()
                                                    .setMenuPresentation(passwordPresentation)
                                                    .build()
                                            )
                                            .setValue(autofillValue)
                                            .build()
                                    )
                                } else {
                                    dataSetBuilder.setValue(
                                        autofillMap[Field.PASSWORD]!!,
                                        autofillValue,
                                        passwordPresentation
                                    )
                                }
                                addDataset(
                                    dataSetBuilder.build()
                                )
                            }
                        }
                        .build()

                    callback.onSuccess(fillResponse)
                }
            }

        } else {
            callback.onSuccess(null)
        }
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        // Not needed for now
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun parseStructure(
        structure: AssistStructure,
        autofillMap: MutableMap<Field, AutofillId>
    ) {
        val windowNodes: List<AssistStructure.WindowNode> =
            structure.run {
                (0 until windowNodeCount).map { getWindowNodeAt(it) }
            }

        windowNodes.forEach { windowNode: AssistStructure.WindowNode ->
            val viewNode: AssistStructure.ViewNode? = windowNode.rootViewNode
            viewNode?.let { traverseNode(it, autofillMap) }
        }
    }

    private fun traverseNode(
        viewNode: AssistStructure.ViewNode,
        autofillMap: MutableMap<Field, AutofillId>
    ) {
        //  if (viewNode.autofillType == View.AUTOFILL_TYPE_TEXT)
        if (viewNode.inputType == TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            viewNode.autofillId?.let {
                autofillMap[Field.PASSWORD] = it
            }
        }


        viewNode.htmlInfo?.attributes?.forEach { attrNameToAttrValue ->
            if (attrNameToAttrValue.first == "type" && attrNameToAttrValue.second == "password") {
                // This is a password field in a web view
                val autofillId = viewNode.autofillId
                if (autofillId != null) {
                    // Store the AutofillId for the password field
                    autofillMap[Field.PASSWORD] = autofillId
                }
            }
        }

        val children: List<AssistStructure.ViewNode> = viewNode.run {
            (0 until childCount).map { getChildAt(it) }
        }

        children.forEach { childNode: AssistStructure.ViewNode ->
            traverseNode(childNode, autofillMap)
        }
    }

    enum class Field {
        PASSWORD,
    }
}
