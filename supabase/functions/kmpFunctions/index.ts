import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const fixedUserId = "02af726d-aa75-4301-a410-049d214841f9";
const jsonHeaders = {
  "Content-Type": "application/json",
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "GET, PUT, OPTIONS",
  "Access-Control-Allow-Headers": "authorization, apikey, content-type",
};

type GenericResponse<T> = {
  data: T | null;
  message: string;
  status: boolean;
};

type DiaryQuote = {
  date: string;
  quote: string;
  createdAt: string;
  updatedAt: string;
};

const supabase = createClient(
  Deno.env.get("SUPABASE_URL")!,
  Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!,
);

function response<T>(
  data: T | null,
  message: string,
  status: boolean,
  statusCode = 200,
): Response {
  const body: GenericResponse<T> = { data, message, status };
  return new Response(JSON.stringify(body), {
    status: statusCode,
    headers: jsonHeaders,
  });
}

function dateFromRequest(request: Request): string | null {
  const segments = new URL(request.url).pathname.split("/").filter(Boolean);
  const daysIndex = segments.indexOf("days");
  const date = daysIndex >= 0 ? segments[daysIndex + 1] : null;
  return date && /^\d{4}-\d{2}-\d{2}$/.test(date) ? date : null;
}

function mapQuote(row: {
  quote_date: string;
  quote: string;
  created_at: string;
  updated_at: string;
}): DiaryQuote {
  return {
    date: row.quote_date,
    quote: row.quote,
    createdAt: row.created_at,
    updatedAt: row.updated_at,
  };
}

async function getQuote(date: string): Promise<Response> {
  const { data, error } = await supabase
    .from("diary_quotes")
    .select("quote_date, quote, created_at, updated_at")
    .eq("user_id", fixedUserId)
    .eq("quote_date", date)
    .maybeSingle();

  if (error) {
    return response(null, error.message, false, 500);
  }

  if (!data) {
    return response(null, "Günün sözü bulunamadı.", false, 404);
  }

  return response(mapQuote(data), "Günün sözü başarıyla getirildi.", true);
}

async function updateQuote(request: Request, date: string): Promise<Response> {
  const body = await request.json().catch(() => null);
  const quote = typeof body?.quote === "string" ? body.quote.trim() : "";

  if (!quote) {
    return response(null, "Quote alanı boş olamaz.", false, 400);
  }

  const { data, error } = await supabase
    .from("diary_quotes")
    .upsert(
      {
        user_id: fixedUserId,
        quote_date: date,
        quote,
        updated_at: new Date().toISOString(),
      },
      { onConflict: "user_id,quote_date" },
    )
    .select("quote_date, quote, created_at, updated_at")
    .single();

  if (error) {
    return response(null, error.message, false, 500);
  }

  return response(mapQuote(data), "Günün sözü başarıyla kaydedildi.", true);
}

Deno.serve(async (request) => {
  if (request.method === "OPTIONS") {
    return new Response("ok", { headers: jsonHeaders });
  }

  const url = new URL(request.url);
  const date = dateFromRequest(request);
  if (!date || url.pathname.split("/").filter(Boolean).length < 2) {
    return response(null, "Geçerli bir tarih gerekli.", false, 400);
  }

  try {
    if (request.method === "GET") {
      return getQuote(date);
    }

    if (request.method === "PUT") {
      return updateQuote(request, date);
    }

    return response(null, "Method not allowed.", false, 405);
  } catch (error) {
    const message = error instanceof Error ? error.message : "Beklenmeyen hata.";
    return response(null, message, false, 500);
  }
});
