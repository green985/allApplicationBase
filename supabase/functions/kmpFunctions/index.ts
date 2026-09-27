import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const fixedUserId = "02af726d-aa75-4301-a410-049d214841f9";
const jsonHeaders = {
  "Content-Type": "application/json",
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "GET, PUT, POST, OPTIONS",
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

type DiaryEntry = {
  id: string;
  areaId: string;
  text: string;
  entryDate: string;
  createdBy: string | null;
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

function pathSegments(request: Request): string[] {
  return new URL(request.url).pathname.split("/").filter(Boolean);
}

function isQuoteRoute(segments: string[]): boolean {
  const diaryIndex = segments.indexOf("diary");
  return (
    diaryIndex >= 0 &&
    segments[diaryIndex + 1] === "days" &&
    segments[diaryIndex + 3] === "quote"
  );
}

function isCreateEntryRoute(segments: string[]): boolean {
  const diaryIndex = segments.indexOf("diary");
  return diaryIndex >= 0 && segments[diaryIndex + 1] === "entries";
}

function dateFromRequest(request: Request): string | null {
  const segments = pathSegments(request);
  const daysIndex = segments.indexOf("days");
  const date = daysIndex >= 0 ? segments[daysIndex + 1] : null;
  return date && isValidDate(date) ? date : null;
}

function isValidDate(value: unknown): value is string {
  if (typeof value !== "string" || !/^\d{4}-\d{2}-\d{2}$/.test(value)) {
    return false;
  }

  const parsed = new Date(`${value}T00:00:00.000Z`);
  return (
    parsed.toISOString().slice(0, 10) === value
  );
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

function mapEntry(row: {
  id: string;
  area_id: string;
  text: string;
  entry_date: string;
  created_at: string;
  updated_at: string;
}): DiaryEntry {
  return {
    id: row.id,
    areaId: row.area_id,
    text: row.text,
    entryDate: row.entry_date,
    createdBy: null,
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
    return response(null, "Günün sözü getirilemedi.", false, 500);
  }

  if (!data) {
    return response(null, "Günün sözü bulunamadı.", false, 404);
  }

  return response(mapQuote(data), "Günün sözü başarıyla getirildi.", true);
}

async function updateQuote(request: Request, date: string): Promise<Response> {
  const body = await request.json().catch(() => null) as Record<string, unknown> | null;
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
    return response(null, "Günün sözü kaydedilemedi.", false, 500);
  }

  return response(mapQuote(data), "Günün sözü başarıyla kaydedildi.", true);
}

async function createEntry(request: Request): Promise<Response> {
  const body = await request.json().catch(() => null) as Record<string, unknown> | null;
  const areaId = typeof body?.areaId === "string" ? body.areaId.trim() : "";
  const text = typeof body?.text === "string" ? body.text.trim() : "";
  const entryDate = typeof body?.entryDate === "string" ? body.entryDate : "";
  const validAreas = new Set([
    "WORK",
    "BODY",
    "HEALTH",
    "MIND",
    "CHARACTER",
    "PEOPLE",
    "LIFE",
  ]);

  if (!validAreas.has(areaId)) {
    return response(null, "Geçerli bir areaId gerekli.", false, 400);
  }

  if (!text || text.length > 2000) {
    return response(null, "Text boş olamaz ve 2000 karakteri geçemez.", false, 400);
  }

  if (!isValidDate(entryDate)) {
    return response(null, "Geçerli bir entryDate gerekli.", false, 400);
  }

  const { data, error } = await supabase
    .from("diary_entries")
    .insert({
      user_id: fixedUserId,
      entry_date: entryDate,
      area_id: areaId,
      text,
    })
    .select("id, area_id, text, entry_date, created_at, updated_at")
    .single();

  if (error || !data) {
    return response(null, "Diary kaydı oluşturulamadı.", false, 500);
  }

  return response(
    mapEntry(data),
    "Diary kaydı başarıyla oluşturuldu.",
    true,
    201,
  );
}

Deno.serve(async (request) => {
  if (request.method === "OPTIONS") {
    return new Response("ok", { headers: jsonHeaders });
  }

  const segments = pathSegments(request);
  try {
    if (isQuoteRoute(segments)) {
      const date = dateFromRequest(request);
      if (!date) {
        return response(null, "Geçerli bir tarih gerekli.", false, 400);
      }

      if (request.method === "GET") {
        return getQuote(date);
      }

      if (request.method === "PUT") {
        return updateQuote(request, date);
      }
    }

    if (isCreateEntryRoute(segments) && request.method === "POST") {
      return createEntry(request);
    }

    if (!isQuoteRoute(segments) && !isCreateEntryRoute(segments)) {
      return response(null, "Endpoint bulunamadı.", false, 404);
    }

    return response(null, "Method not allowed.", false, 405);
  } catch {
    return response(null, "Beklenmeyen bir sunucu hatası oluştu.", false, 500);
  }
});
