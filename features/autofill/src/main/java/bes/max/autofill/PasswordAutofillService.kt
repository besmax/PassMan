package bes.max.autofill

import android.app.assist.AssistStructure
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.FillCallback
import android.service.autofill.FillContext
import android.service.autofill.FillRequest
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
import android.view.autofill.AutofillId
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PasswordAutofillService : AutofillService() {

    @Inject
    lateinit var cipher: CipherApi

    @Inject
    lateinit var siteInfoRepository: SiteInfoRepository

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


    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        TODO("Not yet implemented")
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
